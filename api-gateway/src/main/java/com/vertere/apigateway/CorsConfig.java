package com.vertere.apigateway;  //which folder/namespace this class belongs to

import org.springframework.beans.factory.annotation.Value;   //injects a value from application.properties/env
import org.springframework.context.annotation.Configuration;   //tells Spring "this class defines beans/config"
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * This class configures which frontend origins are allowed to call the
 * gateway from a browser. The allowed origin(s) come from config rather
 * than being hardcoded, since the deployed frontend lives at a different
 * origin than the local dev server.
 *
 * - allowedOrigins: a comma-separated list from `cors.allowed-origins`,
 *   defaulting to just the local Vite dev server. In production this is
 *   set to the real frontend's origin (e.g. a GitHub Pages URL) via the
 *   CORS_ALLOWED_ORIGINS env var - multiple origins can be listed at
 *   once (e.g. both the dev and prod origins) if needed.
 * - addCorsMappings: applies those origins only to /api/**, allowing the
 *   HTTP methods and headers the frontend actually uses.
 */
@Configuration   //tells Spring "this class defines beans/config to load at startup"
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public CorsConfig(@Value("${cors.allowed-origins:http://localhost:5173}") String allowedOrigins) {
        this.allowedOrigins = allowedOrigins.split(",");   //supports one or more comma-separated origins
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*");
    }
}
