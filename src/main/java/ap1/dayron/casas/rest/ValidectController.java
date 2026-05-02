package ap1.dayron.casas.rest;

import ap1.dayron.casas.model.EmailRequest;
import ap1.dayron.casas.model.EmailVerification;
import ap1.dayron.casas.service.ValidectService;
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
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "CRUD de verificaciones de email usando Validect API")
public class ValidectController {

    private final ValidectService validectService;

    // ── CREATE ───────────────────────────────────────────────────────────────

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Verificar email", description = "Llama a Validect API, verifica el email y guarda el resultado en la BD")
    public Mono<EmailVerification> verifyEmail(@RequestBody EmailRequest request) {
        return validectService.verifyEmail(request.getEmail());
    }

    // ── READ ─────────────────────────────────────────────────────────────────

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historial de verificaciones", description = "Lista todos los registros activos ordenados por fecha")
    public Flux<EmailVerification> getHistory() {
        return validectService.getHistory();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar por ID", description = "Retorna un registro de verificación por su ID")
    public Mono<EmailVerification> findById(
            @Parameter(description = "ID del registro", required = true)
            @PathVariable String id) {
        return validectService.findById(id);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar email", description = "Cambia el email del registro, llama a Validect con el nuevo email y actualiza el resultado")
    public Mono<EmailVerification> updateEmail(
            @Parameter(description = "ID del registro a actualizar", required = true)
            @PathVariable String id,
            @RequestBody EmailRequest request) {
        return validectService.updateEmail(id, request.getEmail());
    }

    // ── DELETE (lógico) ──────────────────────────────────────────────────────

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Eliminar registro", description = "Borrado lógico: marca el registro como eliminado sin borrarlo de la BD")
    public Mono<EmailVerification> softDelete(
            @Parameter(description = "ID del registro a eliminar", required = true)
            @PathVariable String id) {
        return validectService.softDelete(id);
    }

    // ── RESTORE ──────────────────────────────────────────────────────────────

    @PatchMapping(value = "/{id}/restore", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Restaurar registro", description = "Restaura un registro previamente eliminado")
    public Mono<EmailVerification> restore(
            @Parameter(description = "ID del registro a restaurar", required = true)
            @PathVariable String id) {
        return validectService.restore(id);
    }
}
