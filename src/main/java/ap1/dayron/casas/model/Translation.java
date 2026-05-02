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
@Document(collection = "translations")
public class Translation {

    @Id
    private String id;
    private String originalText;
    private String translatedText;
    private String fromLanguage;
    private String toLanguage;
    private LocalDateTime translatedAt;

    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime deletedAt;
}
