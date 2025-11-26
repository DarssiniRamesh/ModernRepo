package com.example.postgresdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PUBLIC_INTERFACE
 * SwaggerRedirectController provides convenience redirects to the actual Swagger UI path.
 *
 * Swagger UI is configured at /docs through springdoc configuration.
 * This controller maps common paths (/swagger-ui, /swagger-ui.html) to /docs for discoverability.
 */
@Controller
@Tag(name = "Swagger", description = "Swagger UI redirects and documentation helpers")
public class SwaggerRedirectController {

    // PUBLIC_INTERFACE
    @GetMapping({"/swagger-ui", "/swagger-ui.html"})
    @Operation(
            summary = "Redirect to Swagger UI",
            description = "Redirects commonly used paths /swagger-ui and /swagger-ui.html to the configured Swagger UI at /docs.",
            tags = {"Swagger"}
    )
    public ResponseEntity<Void> redirectToDocs() {
        return ResponseEntity.status(302)
                .header("Location", "/docs")
                .build();
    }
}
