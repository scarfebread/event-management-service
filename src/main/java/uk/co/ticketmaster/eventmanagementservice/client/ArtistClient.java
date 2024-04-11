package uk.co.ticketmaster.eventmanagementservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;

@Component
public class ArtistClient {
    private final WebClient client;

    public ArtistClient(WebClient client) {
        this.client = client;
    }

    public Mono<ArtistResponse> getById(String artistId) { // TODO exception handling on clients
        return client
                .get()
                .uri("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/artists.json")
                .retrieve()
                .bodyToFlux(ArtistResponse.class)
                .filter(artist -> artist.id().equals(artistId))
                .next();
    }
}
