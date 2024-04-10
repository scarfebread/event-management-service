package uk.co.ticketmaster.eventmanagementservice.client.response;

import java.util.List;

public record EventResponse(
        String title,
        String id,
        String dateStatus,
        String timeZone,
        String startDate,
        List<Artist> artists,
        Venue venue,
        Boolean hiddenFromSearch
) {
    public record Artist(String id) {}

    public record Venue(String id) { }
}
