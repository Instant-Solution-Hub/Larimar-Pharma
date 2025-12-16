package com.instantsolutions.larimarpharma.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI larimarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Larimar Pharma API")
                        .description("REST APIs for Larimar Pharma Field Executive app")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Instant Solutions")
                                .email("support@instantsolutions.com")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Larimar Documentation")
                        .url("https://instantsolutions.com/docs")
                );
    }
}
