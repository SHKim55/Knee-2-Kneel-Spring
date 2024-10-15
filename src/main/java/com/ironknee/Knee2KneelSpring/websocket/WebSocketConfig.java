package com.ironknee.Knee2KneelSpring.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameWebSocketHandler gameWebSocketHandler;
    private final CustomHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(final GameWebSocketHandler gameWebSocketHandler, final CustomHandshakeInterceptor handshakeInterceptor) {
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/ws/game")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor);
    }
}
