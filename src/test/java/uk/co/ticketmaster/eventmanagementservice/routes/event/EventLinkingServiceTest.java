package uk.co.ticketmaster.eventmanagementservice.routes.event;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.VenueClient;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.event.model.Event;
import uk.co.ticketmaster.eventmanagementservice.routes.venue.Venue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.ARTIST_ID;
import static uk.co.ticketmaster.eventmanagementservice.defaults.EventDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.VENUE_CITY;

class EventLinkingServiceTest {
    @Test
    void givenValidEvent_whenMapEventsToVenues_thenReturnEventWithVenue() {
        var venueClient = mock(VenueClient.class);

        var eventResponse = new EventResponse(
                EVENT_TITLE,
                EVENT_ID,
                EVENT_DATE_STATUS,
                EVENT_TIMEZONE,
                EVENT_START_DATE,
                List.of(new EventResponse.Artist(ARTIST_ID)),
                new EventResponse.Venue(VENUE_ID),
                false
        );
        var venueResponse = new VenueResponse(
                VENUE_NAME,
                VENUE_URL,
                VENUE_CITY,
                VENUE_ID
        );

        var expectedEvent = Event.fromResponse(
                eventResponse,
                Venue.fromResponse(venueResponse)
        );

        when(venueClient.getById(VENUE_ID)).thenReturn(Mono.just(venueResponse));

        var linkingService = new EventLinkingService(venueClient);

        StepVerifier
                .create(linkingService.mapEventsToVenues(eventResponse))
                .assertNext(response -> assertEquals(expectedEvent, response))
                .verifyComplete();
    }

    @Test
    void givenEventWithMissingVenue_whenMapEventsToVenues_thenReturnEventWithoutVenue() {
        var venueClient = mock(VenueClient.class);

        var eventResponse = new EventResponse(
                EVENT_TITLE,
                EVENT_ID,
                EVENT_DATE_STATUS,
                EVENT_TIMEZONE,
                EVENT_START_DATE,
                List.of(new EventResponse.Artist(ARTIST_ID)),
                new EventResponse.Venue(VENUE_ID),
                false
        );

        var expectedEvent = Event.fromResponse(
                eventResponse,
                null
        );

        when(venueClient.getById(VENUE_ID)).thenReturn(Mono.empty());

        var linkingService = new EventLinkingService(venueClient);

        StepVerifier
                .create(linkingService.mapEventsToVenues(eventResponse))
                .assertNext(response -> assertEquals(expectedEvent, response))
                .verifyComplete();
    }
}
