package ap1.dayron.casas.service;

import ap1.dayron.casas.model.EmailVerification;
import ap1.dayron.casas.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ValidectService {

    private final WebClient validectWebClient;
    private final EmailVerificationRepository emailVerificationRepository;

    public ValidectService(@Qualifier("validectWebClient") WebClient validectWebClient,
                           EmailVerificationRepository emailVerificationRepository) {
        this.validectWebClient = validectWebClient;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    /**
     * Verifica el email via Validect, interpreta el resultado
     * y lo guarda en MongoDB como historial.
     */
    public Mono<EmailVerification> verifyEmail(String email) {
        return validectWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/verify")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    String status = String.valueOf(response.getOrDefault("status", "unknown"));
                    String reason = String.valueOf(response.getOrDefault("reason", ""));
                    boolean isValid = "valid".equalsIgnoreCase(status);

                    EmailVerification record = EmailVerification.builder()
                            .email(email)
                            .valid(isValid)
                            .status(status)
                            .reason(reason)
                            .verifiedAt(LocalDateTime.now())
                            .build();

                    return emailVerificationRepository.save(record);
                })
                .onErrorResume(WebClientResponseException.class, ex ->
                    Mono.error(new RuntimeException(
                        "Validect API error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex))
                );
    }

    /** Historial completo ordenado por fecha descendente */
    public Flux<EmailVerification> getHistory() {
        return emailVerificationRepository.findAllByOrderByVerifiedAtDesc();
    }

    /** Historial filtrado por email */
    public Flux<EmailVerification> getHistoryByEmail(String email) {
        return emailVerificationRepository.findByEmail(email);
    }

    /** Solo los correos válidos */
    public Flux<EmailVerification> getValidEmails() {
        return emailVerificationRepository.findByValid(true);
    }

    /** Solo los correos inválidos/falsos */
    public Flux<EmailVerification> getInvalidEmails() {
        return emailVerificationRepository.findByValid(false);
    }
}
