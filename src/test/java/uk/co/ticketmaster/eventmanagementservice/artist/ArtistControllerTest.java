package uk.co.ticketmaster.eventmanagementservice.artist;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArtistControllerTest {
    private static final String ARTIST_ID = "100";

    @Test
    void givenValidArtistId_whenGetArtist_thenReturnArtistWithEvents() {
        var artistId = ARTIST_ID;
        var service = mock(ArtistService.class);
        var artist = new Artist();

        when(service.getArtist(artistId)).thenReturn(Mono.just(artist));

        var controller = new ArtistController(service);

        StepVerifier
                .create(controller.getArtist(artistId))
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatusCode.valueOf(200), responseEntity.getStatusCode());
                    assertEquals(artist, responseEntity.getBody());
                })
                .verifyComplete();
    }
}