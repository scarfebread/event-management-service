package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.ArtistClient;
import uk.co.ticketmaster.eventmanagementservice.client.EventClient;
import uk.co.ticketmaster.eventmanagementservice.client.VenueClient;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.Artist;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.ArtistWithEvents;
import uk.co.ticketmaster.eventmanagementservice.routes.event.Event;
import uk.co.ticketmaster.eventmanagementservice.routes.venue.Venue;

import java.util.List;

import static java.lang.String.format;

@Service
public class ArtistService {
    private final Logger logger = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistClient artistClient;
    private final VenueClient venueClient;
    private final EventClient eventClient;

    public ArtistService(
            ArtistClient artistClient,
            VenueClient venueClient,
            EventClient eventClient
    ) {
        this.artistClient = artistClient;
        this.venueClient = venueClient;
        this.eventClient = eventClient;
    }

    public Mono<Artist> getArtist(String artistId) {
        Mono<ArtistWithEvents> artist = artistClient
                .getById(artistId)
                .map(ArtistWithEvents::fromResponse);

        Mono<List<Event>> events = eventClient
                .getByArtistId(artistId)
                .flatMap(this::mapEventsToVenues)
                .collectList();

        return Mono.zip(
                artist,
                events,
                ArtistWithEvents::withEvents);
    }

    private Mono<Event> mapEventsToVenues(EventResponse eventResponse) {
        return venueClient
                .getById(eventResponse.venue().id())
                .map(venueResponse -> Event.fromResponse(eventResponse, Venue.fromResponse(venueResponse)))
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn(format(
                            "Failed to match venue to venue ID. This implies an upstream data integrity issue. Venue ID: %s",
                            eventResponse.venue().id()
                    ));
                    return Mono.just(Event.fromResponse(eventResponse, null));
                }));
    }
}
