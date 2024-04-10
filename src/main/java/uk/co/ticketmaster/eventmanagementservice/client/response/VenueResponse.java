package uk.co.ticketmaster.eventmanagementservice.client.response;

public record VenueResponse(
        String name,
        String url,
        String city,
        String id
) {}
