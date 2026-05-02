package ap1.dayron.casas.rest;

import ap1.dayron.casas.model.Language;
import ap1.dayron.casas.model.Translation;
import ap1.dayron.casas.service.TranslatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/translator")
@RequiredArgsConstructor
@Tag(name = "Translator", description = "CRUD de traducciones usando Free Google Translator API")
public class TranslatorController {

    private final TranslatorService translatorService;

    // ── CREATE ───────────────────────────────────────────────────────────────

    @PostMapping(value = "/translate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Traducir texto",
        description = "Traduce un texto y guarda el resultado. Idiomas disponibles: ESPANOL, INGLES, FRANCES, ALEMAN, PORTUGUES, ITALIANO, JAPONES, CHINO_SIMPLIFICADO, RUSO, ARABE, COREANO, AUTO (para detectar automaticamente)"
    )
    public Mono<Translation> translate(
            @Parameter(description = "Texto que deseas traducir. Ejemplo: Hola mundo", required = true)
            @RequestParam String text,
            @Parameter(description = "Idioma de origen. Ejemplo: ESPANOL — usa AUTO para detectar automaticamente", required = false)
            @RequestParam(defaultValue = "AUTO") String from,
            @Parameter(description = "Idioma de destino. Ejemplo: INGLES", required = true)
            @RequestParam String to) {
        return translatorService.translate(
                text,
                Language.fromName(from).getCode(),
                Language.fromName(to).getCode()
        );
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historial de traducciones", description = "Lista todos los registros activos ordenados por fecha")
    public Flux<Translation> getHistory() {
        return translatorService.getHistory();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar por ID", description = "Retorna una traduccion por su ID")
    public Mono<Translation> findById(
            @Parameter(description = "ID del registro", required = true)
            @PathVariable String id) {
        return translatorService.findById(id);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Actualizar traduccion",
        description = "Cambia el texto e idiomas, re-llama la API y actualiza el registro. Idiomas disponibles: ESPANOL, INGLES, FRANCES, ALEMAN, PORTUGUES, ITALIANO, JAPONES, CHINO_SIMPLIFICADO, RUSO, ARABE, COREANO, AUTO"
    )
    public Mono<Translation> update(
            @Parameter(description = "ID del registro a actualizar", required = true)
            @PathVariable String id,
            @Parameter(description = "Nuevo texto a traducir. Ejemplo: Buenos dias", required = true)
            @RequestParam String text,
            @Parameter(description = "Idioma de origen. Ejemplo: ESPANOL — usa AUTO para detectar automaticamente", required = false)
            @RequestParam(defaultValue = "AUTO") String from,
            @Parameter(description = "Idioma de destino. Ejemplo: INGLES", required = true)
            @RequestParam String to) {
        return translatorService.update(
                id,
                text,
                Language.fromName(from).getCode(),
                Language.fromName(to).getCode()
        );
    }

    // ── DELETE (logico) ──────────────────────────────────────────────────────

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar traduccion", description = "Borrado logico: marca el registro como eliminado sin borrarlo de la BD")
    public Mono<Translation> softDelete(
            @Parameter(description = "ID del registro a eliminar", required = true)
            @PathVariable String id) {
        return translatorService.softDelete(id);
    }

    // ── RESTORE ──────────────────────────────────────────────────────────────

    @PatchMapping(value = "/{id}/restore", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Restaurar traduccion", description = "Restaura un registro previamente eliminado")
    public Mono<Translation> restore(
            @Parameter(description = "ID del registro a restaurar", required = true)
            @PathVariable String id) {
        return translatorService.restore(id);
    }
}
