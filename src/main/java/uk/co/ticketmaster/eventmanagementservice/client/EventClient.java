package uk.co.ticketmaster.eventmanagementservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;

@Component
public class EventClient {
    private final WebClient client;

    public EventClient(WebClient client) {
        this.client = client;
    }

    public Flux<EventResponse> getByArtistId(String artistId) {
        return client
                .get()
                .uri("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/events.json")
                .retrieve()
                    .bodyToFlux(EventResponse.class)
                    .filter(event -> event
                            .artists()
                            .stream()
                            .map(EventResponse.Artist::id)
                            .toList()
                            .contains(artistId))
                .onErrorResume(e -> Mono.error(new WebClientException("Error retrieving events", e)));
    }
}
