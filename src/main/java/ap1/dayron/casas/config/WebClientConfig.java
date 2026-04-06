package ap1.dayron.casas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.key-validect}")
    private String rapidApiKeyValidect;

    @Value("${rapidapi.shazam.host}")
    private String shazamHost;

    @Value("${rapidapi.shazam.base-url}")
    private String shazamBaseUrl;

    @Value("${rapidapi.validect.host}")
    private String validectHost;

    @Value("${rapidapi.validect.base-url}")
    private String validectBaseUrl;

    @Bean("shazamWebClient")
    public WebClient shazamWebClient() {
        return WebClient.builder()
                .baseUrl(shazamBaseUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKey)
                .defaultHeader("x-rapidapi-host", shazamHost)
                .build();
    }

    @Bean("validectWebClient")
    public WebClient validectWebClient() {
        return WebClient.builder()
                .baseUrl(validectBaseUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKeyValidect)
                .defaultHeader("x-rapidapi-host", validectHost)
                .build();
    }
}
