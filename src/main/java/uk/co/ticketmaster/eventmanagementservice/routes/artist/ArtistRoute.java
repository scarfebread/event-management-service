package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ArtistRoute {
    private final Logger logger = LoggerFactory.getLogger(ArtistRoute.class);
    private final ArtistService artistService;

    public ArtistRoute(ArtistService artistService) {
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
                        .onErrorResume(e -> {
                            logger.error(format("Error getting artist on path: /artist/%s", request.pathVariable("id")), e);
                            return ServerResponse
                                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .bodyValue(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
                        })
        );
    }
}
