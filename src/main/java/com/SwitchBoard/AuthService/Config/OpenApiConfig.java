package com.SwitchBoard.AuthService.Config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration using annotations (simpler and more reliable approach)
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Authentication and Account Management Service API",
                description = "API documentation for the AuthAMS Service - handles user authentication, OTP management, and account profiles.",
                version = "1.0.0",
                contact = @Contact(
                        name = "SWITCHBOARD",
                        email = "support@lamicons.com",
                        url = "https://www.lamicons.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "/", description = "Default Server URL")
        }
)
public class OpenApiConfig {
    // Using annotation-based configuration instead of bean-based
}