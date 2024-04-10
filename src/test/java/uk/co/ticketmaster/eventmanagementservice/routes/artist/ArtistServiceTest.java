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

class ArtistServiceTest {
    private static final String ARTIST_ID = "100";
    private static final String ARTIST_NAME = "ARTIST_NAME";
    private static final String ARTIST_IMG = "ARTIST_IMG";
    private static final String ARTIST_URL = "ARTIST_URL";
    private static final Integer ARTIST_RANK = 1;

    private static final String EVENT_TITLE = "EVENT_TITLE";
    private static final String EVENT_ID = "10";
    private static final String EVENT_DATE_STATUS = "EVENT_DATE_STATUS";
    private static final String EVENT_TIMEZONE = "EVENT_TIMEZONE";
    private static final String EVENT_START_DATE = "EVENT_START_DATE";

    private static final String VENUE_NAME = "VENUE_NAME";
    private static final String VENUE_URL = "VENUE_URL";
    private static final String VENUE_CITY = "VENUE_CITY";
    private static final String VENUE_ID = "20";

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
