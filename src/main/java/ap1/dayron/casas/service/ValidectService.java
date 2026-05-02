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
    private final EmailVerificationRepository repo;

    public ValidectService(@Qualifier("validectWebClient") WebClient validectWebClient,
                           EmailVerificationRepository repo) {
        this.validectWebClient = validectWebClient;
        this.repo = repo;
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    /** Llama a Validect API, guarda y retorna */
    public Mono<EmailVerification> verifyEmail(String email) {
        return callValidect(email).flatMap(repo::save);
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /** Lista todos los registros activos ordenados por número */
    public Flux<EmailVerification> getHistory() {
        return repo.findByDeletedFalseOrderByVerifiedAtDesc();
    }

    /** Busca un registro activo por ID */
    public Mono<EmailVerification> findById(String id) {
        return repo.findById(id)
                .filter(e -> !e.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /** Cambia el email, llama a Validect con el nuevo email y actualiza el registro */
    public Mono<EmailVerification> updateEmail(String id, String newEmail) {
        return repo.findById(id)
                .filter(e -> !e.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)))
                .flatMap(existing -> callValidect(newEmail)
                        .map(updated -> {
                            existing.setEmail(newEmail);
                            existing.setValid(updated.isValid());
                            existing.setStatus(updated.getStatus());
                            existing.setReason(updated.getReason());
                            existing.setVerifiedAt(updated.getVerifiedAt());
                            return existing;
                        }))
                .flatMap(repo::save);
    }

    // ── DELETE (lógico) ──────────────────────────────────────────────────────

    /** Marca el registro como eliminado sin borrarlo de la BD */
    public Mono<EmailVerification> softDelete(String id) {
        return repo.findById(id)
                .filter(e -> !e.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)))
                .flatMap(e -> {
                    e.setDeleted(true);
                    e.setDeletedAt(LocalDateTime.now());
                    return repo.save(e);
                });
    }

    // ── RESTORE ──────────────────────────────────────────────────────────────

    /** Restaura un registro previamente eliminado */
    public Mono<EmailVerification> restore(String id) {
        return repo.findById(id)
                .filter(EmailVerification::isDeleted)
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado o no está eliminado: " + id)))
                .flatMap(e -> {
                    e.setDeleted(false);
                    e.setDeletedAt(null);
                    return repo.save(e);
                });
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private Mono<EmailVerification> callValidect(String email) {
        return validectWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/verify")
                        .queryParam("email", email)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resp = (Map<String, Object>) response;
                    String status = String.valueOf(resp.getOrDefault("status", "unknown"));
                    String reason = String.valueOf(resp.getOrDefault("reason", ""));
                    return EmailVerification.builder()
                            .email(email)
                            .valid("valid".equalsIgnoreCase(status))
                            .status(status)
                            .reason(reason)
                            .verifiedAt(LocalDateTime.now())
                            .build();
                })
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.error(new RuntimeException(
                                "Validect API error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex)));
    }
}
