package uk.co.ticketmaster.eventmanagementservice.routes.artist;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import uk.co.ticketmaster.eventmanagementservice.routes.artist.model.Artist;

@RestController
@RequestMapping(path = "/artist")
public class ArtistController {
    private final ArtistService artistService;

    public ArtistController(
        ArtistService artistService
    ) {
        this.artistService = artistService;
    }

    @GetMapping("/{artistId}")
    public Mono<ResponseEntity<Artist>> getArtist(@PathVariable @NonNull String artistId) {
        return artistService
                .getArtist(artistId)
                .map(ResponseEntity::ok);
    }
}
