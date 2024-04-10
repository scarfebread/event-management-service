package uk.co.ticketmaster.eventmanagementservice.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
