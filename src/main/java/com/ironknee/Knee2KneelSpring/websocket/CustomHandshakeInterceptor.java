package com.ironknee.Knee2KneelSpring.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // JWT 토큰 또는 HttpSession을 통해 사용자 정보를 세션에 저장
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // 세션에서 사용자 ID를 가져와 WebSocket 세션에 저장
            String username = (String) servletRequest.getSession().getAttribute("username");
            attributes.put("username", username);  // WebSocket 세션에 사용자 정보 저장
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
