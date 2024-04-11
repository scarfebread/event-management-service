package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.ArtistClient;
import uk.co.ticketmaster.eventmanagementservice.client.EventClient;
import uk.co.ticketmaster.eventmanagementservice.client.VenueClient;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.ArtistWithEvents;
import uk.co.ticketmaster.eventmanagementservice.routes.event.Event;
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
        var venueClient = mock(VenueClient.class);

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
        when(venueClient.getById(VENUE_ID)).thenReturn(Mono.just(venueResponse));

        var artistService = new ArtistService(artistClient, venueClient, eventClient);

        StepVerifier
                .create(artistService.getArtist(ARTIST_ID))
                .assertNext(response -> assertEquals(expectedArtist, response))
                .verifyComplete();
    }
}
