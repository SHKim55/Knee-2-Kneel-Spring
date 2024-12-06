package com.ironknee.Knee2KneelSpring.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private String getUsernameFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("username");
    }

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        System.out.println(session.getAttributes());
//        String username = getUsernameFromSession(session);
//        System.out.println("username sessioned : " + username);
//        sessions.put(username, session);
//    }
//
//    @Override
//    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//
//    }
//
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
//        String username = getUsernameFromSession(session);
//        sessions.remove(username);
//    }
//
//    @Override
//    public boolean supportsPartialMessages() {
//        return false;
//    }

    public void sendMessageToClient(String username, String message) throws IOException {
        WebSocketSession session = sessions.get(username);
        if(session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.out.println("Error occurred in sending message to user : " + username);
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMessageToAll(String message) {
        sessions.forEach(
                (username, session) -> {
                    if(session != null && session.isOpen()) {
                        try {
                            session.sendMessage(new TextMessage(message));
                        } catch (IOException e) {
                            System.out.println("Error occurred in sending message to user : " + username);
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }
}