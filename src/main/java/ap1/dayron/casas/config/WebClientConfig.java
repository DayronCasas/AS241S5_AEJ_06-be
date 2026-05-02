package ap1.dayron.casas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.key-validect}")
    private String rapidApiKeyValidect;

    @Value("${rapidapi.key-translator}")
    private String rapidApiKeyTranslator;

    @Value("${rapidapi.translator.host}")
    private String translatorHost;

    @Value("${rapidapi.translator.base-url}")
    private String translatorBaseUrl;

    @Value("${rapidapi.validect.host}")
    private String validectHost;

    @Value("${rapidapi.validect.base-url}")
    private String validectBaseUrl;

    @Bean("translatorWebClient")
    public WebClient translatorWebClient() {
        return WebClient.builder()
                .baseUrl(translatorBaseUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKeyTranslator)
                .defaultHeader("x-rapidapi-host", translatorHost)
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
