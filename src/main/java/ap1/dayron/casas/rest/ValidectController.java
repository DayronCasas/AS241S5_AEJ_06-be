package ap1.dayron.casas.rest;

import ap1.dayron.casas.model.EmailVerification;
import ap1.dayron.casas.service.ValidectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "Verificación de emails y historial de verificaciones")
public class ValidectController {

    private final ValidectService validectService;

    @GetMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Verificar email", description = "Verifica si un email es real o falso/inexistente y guarda el resultado")
    public Mono<EmailVerification> verifyEmail(
            @Parameter(description = "Email a verificar", required = true)
            @RequestParam String email) {
        return validectService.verifyEmail(email);
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historial completo", description = "Todos los correos verificados ordenados por fecha")
    public Flux<EmailVerification> getHistory() {
        return validectService.getHistory();
    }

    @GetMapping(value = "/history/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historial por email", description = "Verificaciones anteriores de un correo específico")
    public Flux<EmailVerification> getHistoryByEmail(
            @Parameter(description = "Email a consultar", required = true)
            @PathVariable("email") String email) {
        return validectService.getHistoryByEmail(email);
    }

    @GetMapping(value = "/history/valid", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Correos válidos", description = "Lista solo los correos que resultaron válidos/reales")
    public Flux<EmailVerification> getValidEmails() {
        return validectService.getValidEmails();
    }

    @GetMapping(value = "/history/invalid", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Correos inválidos", description = "Lista solo los correos falsos o inexistentes")
    public Flux<EmailVerification> getInvalidEmails() {
        return validectService.getInvalidEmails();
    }
}
