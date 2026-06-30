package com.marketplace.ms_autenticacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI para ms-autenticacion.
 * Swagger UI: http://localhost:PUERTO/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS-Autenticacion API")
                        .version("1.0")
                        .description("Login, sesiones y tokens"));
    }
}
