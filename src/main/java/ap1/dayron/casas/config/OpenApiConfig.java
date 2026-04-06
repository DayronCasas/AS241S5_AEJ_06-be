package ap1.dayron.casas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shazam & Email Verification API")
                        .version("1.0.0")
                        .description("API RESTful reactiva — reconocimiento de canciones por audio (Shazam) y verificación de emails (Validect)")
                        .contact(new Contact()
                                .name("Dayron Casas")
                                .email("dayron@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
