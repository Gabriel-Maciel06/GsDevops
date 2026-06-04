package com.gs.agroid.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("AgroID — API de Monitoramento Agrícola Inteligente")
                        .description("API REST para orquestração de sensores IoT (ESP32), dados de satélite "
                                + "e irrigação automatizada. Autenticação via JWT (Bearer Token).\n\n"
                                + "**Como usar:**\n"
                                + "1. Registre um usuário em `POST /api/auth/register`\n"
                                + "2. Faça login em `POST /api/auth/login` para obter o token\n"
                                + "3. Clique no botão **Authorize 🔒** acima e cole o token retornado\n"
                                + "4. Agora todos os endpoints estarão acessíveis!")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gabriel Maciel")
                                .email("gabriel@fiap.com.br"))
                        .license(new License()
                                .name("Licença Acadêmica FIAP")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira aqui o token JWT obtido no endpoint /api/auth/login. "
                                                + "Exemplo: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                        ));
    }
}
