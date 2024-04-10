package uk.co.ticketmaster.eventmanagementservice.routes.artist.model;

import uk.co.ticketmaster.eventmanagementservice.client.response.ArtistResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.event.Event;

import java.util.List;
import java.util.Objects;

public class ArtistWithEvents extends Artist {
    public List<Event> events;

    protected ArtistWithEvents(String name, String id, String imgSrc, String url, Integer rank) {
        super(name, id, imgSrc, url, rank);
    }

    public static ArtistWithEvents fromResponse(ArtistResponse response) {
        return new ArtistWithEvents(
                response.name(),
                response.id(),
                response.imgSrc(),
                response.url(),
                response.rank()
        );
    }

    public Artist withEvents(List<Event> events) {
        this.events = events;
        return this;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof ArtistWithEvents that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(events, that.events);
    }
}
