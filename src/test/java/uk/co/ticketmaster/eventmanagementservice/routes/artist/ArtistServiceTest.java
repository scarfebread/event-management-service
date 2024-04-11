package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.ArtistClient;
import uk.co.ticketmaster.eventmanagementservice.client.EventClient;
import uk.co.ticketmaster.eventmanagementservice.client.WebClientException;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.ArtistWithEvents;
import uk.co.ticketmaster.eventmanagementservice.routes.event.EventLinkingService;
import uk.co.ticketmaster.eventmanagementservice.routes.event.model.Event;
import uk.co.ticketmaster.eventmanagementservice.routes.venue.Venue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.EventDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.*;

class ArtistServiceTest {
    @Test
    void givenValidArtistId_whenGetArtist_thenReturnArtistWithEvents() {
        var artistClient = mock(ArtistClient.class);
        var eventClient = mock(EventClient.class);
        var linkingService = mock(EventLinkingService.class);

        var artistResponse = new ArtistResponse(ARTIST_ID, ARTIST_NAME, ARTIST_IMG, ARTIST_URL, ARTIST_RANK);
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

        var expectedArtist = ArtistWithEvents
                .fromResponse(artistResponse)
                .withEvents(List.of(
                        Event.fromResponse(
                                eventResponse,
                                Venue.fromResponse(venueResponse)
                        )
                ));

        when(artistClient.getById(ARTIST_ID)).thenReturn(Mono.just(artistResponse));
        when(eventClient.getByArtistId(ARTIST_ID)).thenReturn(Flux.just(eventResponse));
        when(linkingService.mapEventsToVenues(eventResponse)).thenReturn(Mono.just(
                Event.fromResponse(
                        eventResponse,
                        Venue.fromResponse(venueResponse)
                )
        ));

        var artistService = new ArtistService(artistClient, eventClient, linkingService);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .assertNext(response -> assertEquals(expectedArtist, response))
                .verifyComplete();
    }

    @Test
    void givenValidArtistIdWithNoEvents_whenGetArtist_thenReturnArtistWithoutEvents() {
        var artistClient = mock(ArtistClient.class);
        var eventClient = mock(EventClient.class);
        var linkingService = mock(EventLinkingService.class);

        var artistResponse = new ArtistResponse(ARTIST_ID, ARTIST_NAME, ARTIST_IMG, ARTIST_URL, ARTIST_RANK);

        var expectedArtist = ArtistWithEvents
                .fromResponse(artistResponse)
                .withEvents(List.of());

        when(artistClient.getById(ARTIST_ID)).thenReturn(Mono.just(artistResponse));
        when(eventClient.getByArtistId(ARTIST_ID)).thenReturn(Flux.empty());

        var artistService = new ArtistService(artistClient, eventClient, linkingService);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .assertNext(response -> assertEquals(expectedArtist, response))
                .verifyComplete();
    }

    @Test
    void givenEventsWithArtistIdButNoArist_whenGetArtist_thenReturnEmptyMono() {
        var artistClient = mock(ArtistClient.class);
        var eventClient = mock(EventClient.class);
        var linkingService = mock(EventLinkingService.class);

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

        when(artistClient.getById(ARTIST_ID)).thenReturn(Mono.empty());
        when(eventClient.getByArtistId(ARTIST_ID)).thenReturn(Flux.just(eventResponse));

        var artistService = new ArtistService(artistClient, eventClient, linkingService);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void givenAnArtistIdThatDoesNotExist_whenGetArtist_thenReturnEmptyMono() {
        var artistClient = mock(ArtistClient.class);
        var eventClient = mock(EventClient.class);
        var linkingService = mock(EventLinkingService.class);

        when(artistClient.getById(ARTIST_ID)).thenReturn(Mono.empty());
        when(eventClient.getByArtistId(ARTIST_ID)).thenReturn(Flux.empty());

        var artistService = new ArtistService(artistClient, eventClient, linkingService);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void givenAClientError_whenGetArtist_thenReturnUpstreamException() {
        var artistClient = mock(ArtistClient.class);
        var eventClient = mock(EventClient.class);
        var linkingService = mock(EventLinkingService.class);

        when(artistClient.getById(ARTIST_ID)).thenReturn(Mono.error(new WebClientException("Error retrieving artists", new RuntimeException())));
        when(eventClient.getByArtistId(ARTIST_ID)).thenReturn(Flux.empty());

        var artistService = new ArtistService(artistClient, eventClient, linkingService);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .expectErrorMatches(e -> e instanceof WebClientException && e.getMessage().equals("Error retrieving artists"))
                .verify();
    }
}
