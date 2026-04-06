package ap1.dayron.casas.service;

import ap1.dayron.casas.model.SongRecognition;
import ap1.dayron.casas.repository.SongRecognitionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ShazamService {

    private final WebClient shazamWebClient;
    private final SongRecognitionRepository songRecognitionRepository;

    public ShazamService(@Qualifier("shazamWebClient") WebClient shazamWebClient,
                         SongRecognitionRepository songRecognitionRepository) {
        this.shazamWebClient = shazamWebClient;
        this.songRecognitionRepository = songRecognitionRepository;
    }

    public Mono<Map> recognizeRaw(Flux<DataBuffer> audioData, String filename) {
        return DataBufferUtils.join(audioData)
                .flatMap(buf -> {
                    byte[] bytes = new byte[buf.readableByteCount()];
                    buf.read(bytes);
                    DataBufferUtils.release(buf);
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("upload_file", bytes)
                            .filename(filename)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM);
                    return shazamWebClient.post()
                            .uri("/shazam/recognize/")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(Map.class);
                });
    }

    @SuppressWarnings("unchecked")
    public Mono<SongRecognition> recognizeSong(Flux<DataBuffer> audioData, String filename) {
        return DataBufferUtils.join(audioData)
                .flatMap(buf -> {
                    byte[] bytes = new byte[buf.readableByteCount()];
                    buf.read(bytes);
                    DataBufferUtils.release(buf);

                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("upload_file", bytes)
                            .filename(filename)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM);

                    return shazamWebClient.post()
                            .uri("/shazam/recognize/")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(Map.class)
                            .flatMap(response -> {
                                // Estructura: { status, result: { track: {...}, matches: [...] } }
                                Map<String, Object> result = (Map<String, Object>) response.get("result");
                                Map<String, Object> track = result != null ? (Map<String, Object>) result.get("track") : null;

                                String title = str(track, "title");
                                String artist = str(track, "subtitle");

                                // coverUrl
                                Map<String, Object> images = track != null ? (Map<String, Object>) track.get("images") : null;
                                String coverUrl = images != null
                                        ? String.valueOf(images.getOrDefault("coverarthq", images.getOrDefault("coverart", "")))
                                        : "";

                                // genre desde genres.primary
                                String genre = "";
                                Map<String, Object> genres = track != null ? (Map<String, Object>) track.get("genres") : null;
                                if (genres != null) genre = String.valueOf(genres.getOrDefault("primary", ""));

                                // album desde sections[type=SONG].metadata[title=Album].text
                                String album = "";
                                List<Map<String, Object>> sections = track != null ? (List<Map<String, Object>>) track.get("sections") : null;
                                if (sections != null) {
                                    for (Map<String, Object> section : sections) {
                                        if ("SONG".equals(section.get("type"))) {
                                            List<Map<String, Object>> metadata = (List<Map<String, Object>>) section.get("metadata");
                                            if (metadata != null) {
                                                for (Map<String, Object> meta : metadata) {
                                                    if ("Album".equals(meta.get("title"))) {
                                                        album = String.valueOf(meta.get("text"));
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }

                                SongRecognition record = SongRecognition.builder()
                                        .title(title)
                                        .artist(artist)
                                        .album(album)
                                        .genre(genre)
                                        .coverUrl(coverUrl)
                                        .recognizedAt(LocalDateTime.now())
                                        .build();

                                return songRecognitionRepository.save(record);
                            })
                            .onErrorResume(WebClientResponseException.class, ex ->
                                    Mono.error(new RuntimeException(
                                            "Shazam API error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex)));
                });
    }

    private String str(Map<String, Object> map, String key) {
        if (map == null) return "Unknown";
        Object val = map.get(key);
        return val != null && !val.toString().isEmpty() ? val.toString() : "Unknown";
    }

    public Flux<SongRecognition> getHistory() {
        return songRecognitionRepository.findAllByOrderByRecognizedAtDesc();
    }

    public Flux<SongRecognition> getByArtist(String artist) {
        return songRecognitionRepository.findByArtistIgnoreCase(artist);
    }
}