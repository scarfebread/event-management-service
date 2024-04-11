package uk.co.ticketmaster.eventmanagementservice.client;

public class WebClientException extends RuntimeException {
    public WebClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
