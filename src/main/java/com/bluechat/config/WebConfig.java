package com.bluechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for CORS settings.
 * Configures Cross-Origin Resource Sharing for HTTP endpoints.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure cross-origin resource sharing (CORS).
     * This is needed for clients from different origins to access the REST API.
     * 
     * @param registry the CorsRegistry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:[*]")  // Allow localhost with any port
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

