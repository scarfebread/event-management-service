package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ArtistRoute {
    private final ArtistService artistService;

    public ArtistRoute(
        ArtistService artistService
    ) {
        this.artistService = artistService;
    }

    @Bean
    RouterFunction<ServerResponse> getArtist() {
        return route(GET("/artist/{id}"),
                request -> artistService
                        .getArtist(request.pathVariable("id"))
                        .flatMap(artist -> ok()
                                .contentType(APPLICATION_JSON)
                                .bodyValue(artist))
                        .switchIfEmpty(notFound().build())
                        // TODO exception handling
        );
    }
}
