package ap1.dayron.casas.rest;

import ap1.dayron.casas.model.SongRecognition;
import ap1.dayron.casas.service.ShazamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/shazam")
@RequiredArgsConstructor
@Tag(name = "Shazam", description = "Reconocimiento de canciones por audio y historial")
public class ShazamController {

    private final ShazamService shazamService;

    @PostMapping(value = "/recognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reconocer canción", description = "Sube un clip de audio (mp3/wav) de 2-10 segundos, identifica la canción y guarda el resultado")
    public Mono<SongRecognition> recognizeSong(@RequestPart("upload_file") FilePart file) {
        String filename = file.filename().isBlank() ? "audio.mp3" : file.filename();
        return shazamService.recognizeSong(file.content(), filename);
    }

    @PostMapping(value = "/recognize/raw", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Respuesta cruda de Shazam", description = "Devuelve la respuesta JSON completa de la API sin procesar — útil para depuración")
    public Mono<Map<String, Object>> recognizeRaw(@RequestPart("upload_file") FilePart file) {
        String filename = file.filename().isBlank() ? "audio.mp3" : file.filename();
        return shazamService.recognizeRaw(file.content(), filename)
                .map(m -> (Map<String, Object>) m);
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historial de canciones", description = "Todas las canciones reconocidas ordenadas por fecha")
    public Flux<SongRecognition> getHistory() {
        return shazamService.getHistory();
    }

    @GetMapping(value = "/history/artist/{artist}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar por artista", description = "Canciones reconocidas filtradas por artista")
    public Flux<SongRecognition> getByArtist(@PathVariable("artist") String artist) {
        return shazamService.getByArtist(artist);
    }
}
