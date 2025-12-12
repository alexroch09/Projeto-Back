package br.edu.lampi.infrareport.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
@OpenAPIDefinition
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT", name = "Authorization", scheme = "Bearer")
public class SwaggerConfig {
    @Bean
      public OpenAPI basOpenAPI() {
            SecurityRequirement securityRequirement = new SecurityRequirement();
            securityRequirement.addList("Authorization");

            return new OpenAPI()
                        .security(List.of(securityRequirement))
                        .info(new Info().title("Infrareport API Documentation").version("1.0.0")
                                    .description("Infrareport api documentation with all endpoints"));
      }
}

