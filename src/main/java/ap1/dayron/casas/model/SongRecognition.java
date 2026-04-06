package ap1.dayron.casas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "song_recognitions")
public class SongRecognition {

    @Id
    private String id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String coverUrl;
    private LocalDateTime recognizedAt;
}
