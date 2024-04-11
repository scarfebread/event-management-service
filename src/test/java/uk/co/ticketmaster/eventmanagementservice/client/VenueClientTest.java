package uk.co.ticketmaster.eventmanagementservice.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;
import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.*;
import static uk.co.ticketmaster.eventmanagementservice.defaults.VenueDefaults.VENUE_ID;

class VenueClientTest {
    @Test
    void givenValidVenue_whenById_thenReturnVenue() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                           [{
                                               "name":"VENUE_NAME",
                                               "url":"VENUE_URL",
                                               "city":"VENUE_CITY",
                                               "id":"20"
                                            }]
                                        """)
                                .build())
                ).build();

        var expectedResponse = new VenueResponse(
                VENUE_NAME,
                VENUE_URL,
                VENUE_CITY,
                VENUE_ID
        );

        var client = new VenueClient(webClient);

        StepVerifier
                .create(client.getById(VENUE_ID))
                .assertNext(response -> assertEquals(expectedResponse, response))
                .verifyComplete();
    }

    @Test
    void givenIdMatchingMultipleVenue_whenGetById_thenReturnTheFirst() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                       [
                                            {
                                               "name":"VENUE_NAME",
                                               "url":"VENUE_URL",
                                               "city":"VENUE_CITY",
                                               "id":"20"
                                            },
                                            {
                                               "name":"ANOTHER_VENUE",
                                               "url":"VENUE_URL",
                                               "city":"VENUE_CITY",
                                               "id":"20"
                                            }
                                        ]
                                    """)
                                .build())
                ).build();

        var expectedResponse = new VenueResponse(
                VENUE_NAME,
                VENUE_URL,
                VENUE_CITY,
                VENUE_ID
        );

        var client = new VenueClient(webClient);

        StepVerifier
                .create(client.getById(VENUE_ID))
                .assertNext(response -> assertEquals(expectedResponse, response))
                .verifyComplete();
    }

    @Test
    void givenAnArtistIdThatDoesNotExist_whenGetArtist_thenReturnEmptyMono() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                       [{
                                           "name":"ANOTHER_VENUE",
                                           "url":"VENUE_URL",
                                           "city":"VENUE_CITY",
                                           "id":"100"
                                        }]
                                    """)
                                .build())
                ).build();

        var client = new VenueClient(webClient);

        StepVerifier
                .create(client.getById(VENUE_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void givenAServerError_whenGetById_thenReturnWebClientException() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                                .header("content-type", "application/json")
                                .build())
                ).build();

        var client = new VenueClient(webClient);

        StepVerifier
                .create(client.getById(VENUE_ID))
                .expectErrorMatches(e -> e instanceof WebClientException && e.getMessage().equals("Error retrieving venues"))
                .verify();
    }
}
