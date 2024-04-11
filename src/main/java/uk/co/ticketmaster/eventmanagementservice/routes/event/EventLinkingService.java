package uk.co.ticketmaster.eventmanagementservice.routes.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.VenueClient;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.event.model.Event;
import uk.co.ticketmaster.eventmanagementservice.routes.venue.Venue;

import static java.lang.String.format;

/**
 * The intention behind this service is to only be responsible for linking requests, to give the calling services
 * the flexibility to still handle responses from the clients.
 */

@Service
public class EventLinkingService {
    private final Logger logger = LoggerFactory.getLogger(EventLinkingService.class);

    private final VenueClient venueClient;

    public EventLinkingService(VenueClient venueClient) {
        this.venueClient = venueClient;
    }
    public Mono<Event> mapEventsToVenues(EventResponse eventResponse) {
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
