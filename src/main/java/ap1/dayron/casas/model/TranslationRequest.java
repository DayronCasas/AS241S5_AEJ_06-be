package ap1.dayron.casas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TranslationRequest {

    @Schema(description = "Texto a traducir", example = "Hola mundo")
    private String text;

    @Schema(description = "Idioma origen", example = "ESPAÑOL", defaultValue = "AUTO")
    private Language from = Language.AUTO;

    @Schema(description = "Idioma destino", example = "INGLÉS")
    private Language to;
}
