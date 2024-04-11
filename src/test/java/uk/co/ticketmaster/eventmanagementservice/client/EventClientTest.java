package uk.co.ticketmaster.eventmanagementservice.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;

import java.util.ArrayList;
import java.util.List;

import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.EventDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.VENUE_ID;

class EventClientTest {
    @Test
    void givenValidArtistId_whenGetByArtistId_thenReturnEvent() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                           [{
                                               "title":"EVENT_TITLE",
                                               "id":"10",
                                               "dateStatus":"EVENT_DATE_STATUS",
                                               "timeZone":"EVENT_TIMEZONE",
                                               "startDate":"EVENT_START_DATE",
                                               "artists":[
                                                  {
                                                     "id":"100"
                                                  },
                                                  {
                                                     "id":"2"
                                                  }
                                               ],
                                               "venue":{
                                                  "id":"20"
                                               },
                                               "hiddenFromSearch":false
                                            }]
                                        """)
                                .build())
                ).build();

        var expectedResponse = new EventResponse(
                EVENT_TITLE,
                EVENT_ID,
                EVENT_DATE_STATUS,
                EVENT_TIMEZONE,
                EVENT_START_DATE,
                List.of(
                        new EventResponse.Artist(ARTIST_ID),
                        new EventResponse.Artist("2")
                ),
                new EventResponse.Venue(VENUE_ID),
                false
        );

        var client = new EventClient(webClient);

        StepVerifier
                .create(client.getByArtistId(ARTIST_ID))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(response -> true)
                .expectRecordedMatches(events -> List.of(expectedResponse).equals(events))
                .verifyComplete();
    }

    @Test
    void givenNoEvents_whenGetByArtistId_thenReturnEmptyFlux() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("[]")
                                .build())
                ).build();

        var client = new ArtistClient(webClient);

        StepVerifier
                .create(client.getById(ARTIST_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void givenAServerError_whenGetByArtistId_thenReturnWebClientException() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                                .header("content-type", "application/json")
                                .build())
                ).build();

        var client = new EventClient(webClient);

        StepVerifier
                .create(client.getByArtistId(ARTIST_ID))
                .expectErrorMatches(e -> e instanceof WebClientException && e.getMessage().equals("Error retrieving events"))
                .verify();
    }
}
