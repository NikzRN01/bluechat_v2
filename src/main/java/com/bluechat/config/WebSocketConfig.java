package com.bluechat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket support.
 * Sets up STOMP endpoints and message broker configuration.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker options.
     * 
     * @param config the MessageBrokerRegistry to configure
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker
        // Client subscribes to these destinations to receive messages
        config.enableSimpleBroker("/topic", "/queue");
        
        // Messages with these prefixes will be routed to @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
        
        // User-specific destinations prefix
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints mapping.
     * 
     * @param registry the StompEndpointRegistry to configure
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The endpoint that clients use to connect to the WebSocket server
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Use patterns instead of origins with wildcard
                .withSockJS(); // Enables SockJS fallback options
    }
}

