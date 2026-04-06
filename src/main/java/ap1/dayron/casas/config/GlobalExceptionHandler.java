package ap1.dayron.casas.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleRuntimeException(RuntimeException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Error desconocido")));
    }
}
