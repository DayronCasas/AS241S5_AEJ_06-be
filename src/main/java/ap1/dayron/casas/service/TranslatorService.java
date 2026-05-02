package ap1.dayron.casas.service;

import ap1.dayron.casas.model.Translation;
import ap1.dayron.casas.repository.TranslationRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TranslatorService {

    private final WebClient translatorWebClient;
    private final TranslationRepository repo;

    public TranslatorService(@Qualifier("translatorWebClient") WebClient translatorWebClient,
                             TranslationRepository repo) {
        this.translatorWebClient = translatorWebClient;
        this.repo = repo;
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    /** Llama a la API, guarda y retorna la traducción */
    public Mono<Translation> translate(String text, String fromCode, String toCode) {
        return callApi(text, fromCode, toCode)
                .flatMap(translated -> {
                    Translation record = Translation.builder()
                            .originalText(text)
                            .translatedText(translated)
                            .fromLanguage(fromCode)
                            .toLanguage(toCode)
                            .translatedAt(LocalDateTime.now())
                            .build();
                    return repo.save(record);
                });
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    /** Lista todos los registros activos ordenados por fecha */
    public Flux<Translation> getHistory() {
        return repo.findByDeletedFalseOrderByTranslatedAtDesc();
    }

    /** Busca un registro activo por ID */
    public Mono<Translation> findById(String id) {
        return repo.findById(id)
                .filter(t -> !t.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    /** Cambia el texto e idiomas, re-llama la API y actualiza el registro */
    public Mono<Translation> update(String id, String newText, String fromCode, String toCode) {
        return repo.findById(id)
                .filter(t -> !t.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)))
                .flatMap(existing -> callApi(newText, fromCode, toCode)
                        .map(translated -> {
                            existing.setOriginalText(newText);
                            existing.setTranslatedText(translated);
                            existing.setFromLanguage(fromCode);
                            existing.setToLanguage(toCode);
                            existing.setTranslatedAt(LocalDateTime.now());
                            return existing;
                        }))
                .flatMap(repo::save);
    }

    // ── DELETE (lógico) ──────────────────────────────────────────────────────

    /** Marca el registro como eliminado sin borrarlo de la BD */
    public Mono<Translation> softDelete(String id) {
        return repo.findById(id)
                .filter(t -> !t.isDeleted())
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado con id: " + id)))
                .flatMap(t -> {
                    t.setDeleted(true);
                    t.setDeletedAt(LocalDateTime.now());
                    return repo.save(t);
                });
    }

    // ── RESTORE ──────────────────────────────────────────────────────────────

    /** Restaura un registro previamente eliminado */
    public Mono<Translation> restore(String id) {
        return repo.findById(id)
                .filter(Translation::isDeleted)
                .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado o no está eliminado: " + id)))
                .flatMap(t -> {
                    t.setDeleted(false);
                    t.setDeletedAt(null);
                    return repo.save(t);
                });
    }

    // ── HELPER ───────────────────────────────────────────────────────────────

    private Mono<String> callApi(String text, String fromCode, String toCode) {
        return translatorWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/external-api/free-google-translator")
                        .queryParam("from", fromCode)
                        .queryParam("to", toCode)
                        .queryParam("query", text)
                        .build())
                .bodyValue(Map.of("translate", "rapidapi"))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resp = (Map<String, Object>) response;
                    return firstNonEmpty(resp, "translation", "trans", "translatedText", "result", "output");
                })
                .onErrorResume(WebClientResponseException.class, ex ->
                        Mono.error(new RuntimeException(
                                "Translator API error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex)));
    }

    private String firstNonEmpty(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null && !val.toString().isBlank()) return val.toString();
        }
        return "";
    }
}
