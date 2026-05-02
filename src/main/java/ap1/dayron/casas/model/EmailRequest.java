package ap1.dayron.casas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmailRequest {

    @Schema(description = "Correo electrónico a verificar", example = "ejemplo@gmail.com")
    private String email;
}
