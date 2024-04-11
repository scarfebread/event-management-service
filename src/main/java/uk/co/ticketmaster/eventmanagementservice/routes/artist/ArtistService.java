package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.ArtistClient;
import uk.co.ticketmaster.eventmanagementservice.client.EventClient;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.Artist;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.ArtistWithEvents;
import uk.co.ticketmaster.eventmanagementservice.routes.event.model.Event;
import uk.co.ticketmaster.eventmanagementservice.routes.event.EventLinkingService;

import java.util.List;

@Service
public class ArtistService {
    private final ArtistClient artistClient;
    private final EventClient eventClient;
    private final EventLinkingService eventLinkingService;

    public ArtistService(
            ArtistClient artistClient,
            EventClient eventClient,
            EventLinkingService eventLinkingService
    ) {
        this.artistClient = artistClient;
        this.eventClient = eventClient;
        this.eventLinkingService = eventLinkingService;
    }

    public Mono<Artist> getArtist(String artistId) {
        Mono<ArtistWithEvents> artist = artistClient
                .getById(artistId)
                .map(ArtistWithEvents::fromResponse);

        Mono<List<Event>> events = eventClient
                .getByArtistId(artistId)
                .flatMap(eventLinkingService::mapEventsToVenues)
                .collectList();

        return Mono.zip(
                artist,
                events,
                ArtistWithEvents::withEvents);
    }
}
