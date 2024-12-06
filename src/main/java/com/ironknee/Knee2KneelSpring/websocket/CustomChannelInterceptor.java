package com.ironknee.Knee2KneelSpring.websocket;

import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CustomChannelInterceptor implements ChannelInterceptor {
    private final Map<String, String> sessions = new ConcurrentHashMap<>();   // (sessionId, username)

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            String username = (String) accessor.getHeader("username");
//            String userToken = accessor.getFirstNativeHeader("Authorization");

            System.out.println(username);

            if (username != null) {
                sessions.put(sessionId, username);
            }
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            String sessionId = accessor.getSessionId();
            sessions.remove(sessionId);
        }

        return message;
    }

    public String getUsernameBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }

    public String getSessionIdByUsername(String username) {
        for(String key : sessions.keySet()) {
            if(sessions.get(key).equals(username))
                return key;
        }
        return null;
    }
}