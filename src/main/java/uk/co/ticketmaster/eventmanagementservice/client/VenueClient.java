package uk.co.ticketmaster.eventmanagementservice.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;

@Component
public class VenueClient {
    private final WebClient client;

    public VenueClient(WebClient client) {
        this.client = client;
    }

    public Mono<VenueResponse> getById(String venueId) {
        return client
                .get()
                .uri("https://iccp-interview-data.s3-eu-west-1.amazonaws.com/78656681/venues.json")
                .retrieve()
                .bodyToFlux(VenueResponse.class)
                .filter(venue -> venue.id().equals(venueId))
                .next();
    }
}
