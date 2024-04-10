package uk.co.ticketmaster.eventmanagementservice.routes.artist.model;

import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;

import java.util.Objects;

public class Artist {
    public final String name;
    public final String id;
    public final String imgSrc;
    public final String url;
    public final Integer rank;

    protected Artist(
            String name,
            String id,
            String imgSrc,
            String url,
            Integer rank
    ) {
        this.name = name;
        this.id = id;
        this.imgSrc = imgSrc;
        this.url = url;
        this.rank = rank;
    }

    public static Artist fromResponse(ArtistResponse response) {
        return new Artist(
                response.name(),
                response.id(),
                response.imgSrc(),
                response.url(),
                response.rank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artist artist)) return false;

        if (!Objects.equals(name, artist.name)) return false;
        if (!Objects.equals(id, artist.id)) return false;
        if (!Objects.equals(imgSrc, artist.imgSrc)) return false;
        if (!Objects.equals(url, artist.url)) return false;
        return Objects.equals(rank, artist.rank);
    }
}
