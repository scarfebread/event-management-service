package uk.co.ticketmaster.eventmanagementservice.artist;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ArtistService {
    public Mono<Artist> getArtist(String artistId) {

        return Mono.just(new Artist());
    }
}
