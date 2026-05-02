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
@Document(collection = "email_verifications")
public class EmailVerification {

    @Id
    private String id;
    private String email;
    private boolean valid;
    private String status;
    private String reason;
    private LocalDateTime verifiedAt;

    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime deletedAt;
}
