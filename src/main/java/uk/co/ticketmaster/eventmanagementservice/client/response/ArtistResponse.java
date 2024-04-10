package uk.co.ticketmaster.eventmanagementservice.client.response;

public record ArtistResponse(
        String name,
        String id,
        String imgSrc,
        String url,
        Integer rank
) {}
