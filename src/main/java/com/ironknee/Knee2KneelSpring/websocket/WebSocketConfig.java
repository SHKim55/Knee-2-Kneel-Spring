package com.ironknee.Knee2KneelSpring.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final GameWebSocketHandler gameWebSocketHandler;
    private final CustomHandshakeInterceptor handshakeInterceptor;
    private final CustomChannelInterceptor channelInterceptor;

    public WebSocketConfig(final GameWebSocketHandler gameWebSocketHandler,
                           final CustomHandshakeInterceptor handshakeInterceptor,
                           final CustomChannelInterceptor channelInterceptor) {
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.handshakeInterceptor = handshakeInterceptor;
        this.channelInterceptor = channelInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor)
//                .withSockJS();
        ;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");     // client에서 subscribe(메시지 수신)를 위해 참조하는 주소

        registry.setApplicationDestinationPrefixes("/pub");       // client에서는 /pub/(MessageMapping) 주소로 메서드 호출
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor);
    }
}
