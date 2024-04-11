package uk.co.ticketmaster.eventmanagementservice.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.*;

class ArtistClientTest {
    @Test
    void givenValidArtistId_whenGetArtist_thenReturnArtist() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                       [{
                                            "name":"ARTIST_NAME",
                                            "id":"100",
                                            "imgSrc":"ARTIST_IMG",
                                            "url":"ARTIST_URL",
                                            "rank":1
                                        }]
                                    """)
                                .build())
                ).build();

        var expectedResponse = new ArtistResponse(ARTIST_NAME, ARTIST_ID, ARTIST_IMG, ARTIST_URL, ARTIST_RANK);

        var client = new ArtistClient(webClient);

        StepVerifier
                .create(client.getById(ARTIST_ID))
                .assertNext(response -> assertEquals(expectedResponse, response))
                .verifyComplete();
    }

    @Test
    void givenIdMatchingMultipleArtist_whenGetArtist_thenReturnTheFirst() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.OK)
                                .header("content-type", "application/json")
                                .body("""
                                       [
                                            {
                                                "name":"ARTIST_NAME",
                                                "id":"100",
                                                "imgSrc":"ARTIST_IMG",
                                                "url":"ARTIST_URL",
                                                "rank":1
                                            },
                                            {
                                                "name":"ANOTHER_ARTIST",
                                                "id":"100",
                                                "imgSrc":"ARTIST_IMG",
                                                "url":"ARTIST_URL",
                                                "rank":1
                                            }
                                        ]
                                    """)
                                .build())
                ).build();

        var expectedResponse = new ArtistResponse(ARTIST_NAME, ARTIST_ID, ARTIST_IMG, ARTIST_URL, ARTIST_RANK);

        var client = new ArtistClient(webClient);

        StepVerifier
                .create(client.getById(ARTIST_ID))
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
                                            "name":"ARTIST_NAME",
                                            "id":"50",
                                            "imgSrc":"ARTIST_IMG",
                                            "url":"ARTIST_URL",
                                            "rank":1
                                        }]
                                    """)
                                .build())
                ).build();

        var client = new ArtistClient(webClient);

        StepVerifier
                .create(client.getById(ARTIST_ID))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void givenAServerError_whenGetArtist_thenReturnWebClientException() {
        var webClient = WebClient.builder()
                .exchangeFunction(clientRequest ->
                        Mono.just(ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                                .header("content-type", "application/json")
                                .build())
                ).build();

        var client = new ArtistClient(webClient);

        StepVerifier
                .create(client.getById(ARTIST_ID))
                .expectErrorMatches(e -> e instanceof WebClientException && e.getMessage().equals("Error retrieving artists"))
                .verify();
    }
}
