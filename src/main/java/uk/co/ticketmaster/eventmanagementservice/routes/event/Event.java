package uk.co.ticketmaster.eventmanagementservice.routes.event;

import uk.co.ticketmaster.eventmanagementservice.client.response.EventResponse;
import uk.co.ticketmaster.eventmanagementservice.routes.venue.Venue;

import java.util.Objects;

public class Event {
    public final String title;
    public final String id;
    public final String dateStatus;
    public final String timeZone;
    public final String startDate;
    public final Venue venue;
    public final Boolean hiddenFromSearch;

    private Event(
            String title,
            String id,
            String dateStatus,
            String timeZone,
            String startDate,
            Venue venue,
            Boolean hiddenFromSearch
    ) {
        this.title = title;
        this.id = id;
        this.dateStatus = dateStatus;
        this.timeZone = timeZone;
        this.startDate = startDate;
        this.venue = venue;
        this.hiddenFromSearch = hiddenFromSearch;
    }

    public static Event fromResponse(EventResponse response, Venue venue) {
        return new Event(
                response.title(),
                response.id(),
                response.dateStatus(),
                response.timeZone(),
                response.startDate(),
                venue,
                response.hiddenFromSearch()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;

        if (!Objects.equals(title, event.title)) return false;
        if (!Objects.equals(id, event.id)) return false;
        if (!Objects.equals(dateStatus, event.dateStatus)) return false;
        if (!Objects.equals(timeZone, event.timeZone)) return false;
        if (!Objects.equals(startDate, event.startDate)) return false;
        if (!Objects.equals(venue, event.venue)) return false;
        return Objects.equals(hiddenFromSearch, event.hiddenFromSearch);
    }
}
