package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.WebClientException;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.Artist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.ticketmaster.eventmanagementservice.defaults.ArtistDefaults.*;

class ArtistRouteTest {
    @Test
    void givenValidArtistId_whenGetArtist_thenReturnArtistWithEvents() {
        var service = mock(ArtistService.class);
        var artist = Artist.fromResponse(
                new ArtistResponse(ARTIST_NAME, ARTIST_ID, ARTIST_IMG, ARTIST_URL, ARTIST_RANK)
        );

        when(service.getArtist(ARTIST_ID)).thenReturn(Mono.just(artist));

        var artistRoute = new ArtistRoute(service);

        WebTestClient client = WebTestClient.bindToRouterFunction(artistRoute.getArtist()).build();

        client.get().uri("/artist/" + ARTIST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody(Artist.class)
                    .isEqualTo(artist);
    }

    @Test
    void givenAnArtistIdThatDoesNotExist_whenGetArtist_thenReturn404() {
        var service = mock(ArtistService.class);

        when(service.getArtist(ARTIST_ID)).thenReturn(Mono.empty());

        var artistRoute = new ArtistRoute(service);

        WebTestClient client = WebTestClient.bindToRouterFunction(artistRoute.getArtist()).build();

        client.get().uri("/artist/" + ARTIST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void givenAServerError_whenGetArtist_thenReturn500() {
        var service = mock(ArtistService.class);

        when(service.getArtist(ARTIST_ID)).thenThrow(WebClientException.class);

        var artistRoute = new ArtistRoute(service);

        WebTestClient client = WebTestClient.bindToRouterFunction(artistRoute.getArtist()).build();

        client.get().uri("/artist/" + ARTIST_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
