package com.ironknee.Knee2KneelSpring.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {
    Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        sessionMap.put(sessionId, session);
        System.out.println("Session id : " + sessionId);

        super.afterConnectionEstablished(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // 메시지 처리 코드 (ResponseObject 반환 예정)

        // Object Mapping Test
        WebSocketData data = objectMapper.readValue(payload, WebSocketData.class);
        System.out.println("action in response : " + data.getAction());
        System.out.println("data in response : " + data.getData().toString());


        for(WebSocketSession webSocketSession : sessionMap.values()) {
//            webSocketSession.sendMessage(new TextMessage(responseObject.toString()));
            webSocketSession.sendMessage(new TextMessage(data.getAction() + " " + data.getData().toString()));
        }
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();

        // 메시지 처리 코드 (ResponseObject 반환)

        sessionMap.remove(sessionId);
        for(WebSocketSession webSocketSession : sessionMap.values()) {
//            webSocketSession.sendMessage(new TextMessage(responseObject.toString()));
            webSocketSession.sendMessage(new TextMessage("{\"name\" : \"Data Transferred\"}"));
        }

        System.out.println("Remaining session number : " + sessionMap.size());
        super.afterConnectionClosed(session, status);
    }
}
