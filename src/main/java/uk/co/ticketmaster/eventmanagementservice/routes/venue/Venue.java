package uk.co.ticketmaster.eventmanagementservice.routes.venue;

import uk.co.ticketmaster.eventmanagementservice.client.response.VenueResponse;

import java.util.Objects;

public class Venue {
    public final String name;
    public final String url;
    public final String city;
    public final String id;

    private Venue(String name, String url, String city, String id) {
        this.name = name;
        this.url = url;
        this.city = city;
        this.id = id;
    }

    public static Venue fromResponse(VenueResponse venueResponse) {
        return new Venue(
                venueResponse.name(),
                venueResponse.url(),
                venueResponse.city(),
                venueResponse.id()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venue venue)) return false;

        if (!Objects.equals(name, venue.name)) return false;
        if (!Objects.equals(url, venue.url)) return false;
        if (!Objects.equals(city, venue.city)) return false;
        return Objects.equals(id, venue.id);
    }
}
