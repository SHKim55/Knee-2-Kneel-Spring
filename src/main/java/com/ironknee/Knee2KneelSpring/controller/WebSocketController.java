package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameChatDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;

    // enter를 부른 client의 id를 Key로, gameId를 Value로 만든 map을 저장하고 서버에서 메시지를 선별적으로 보내는데 활용
    private Map<String, Long> sessionMap = new HashMap<>();

    public WebSocketController(final SimpMessagingTemplate simpMessagingTemplate,
                               final GameService gameService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameService = gameService;
    }

    @MessageMapping("/game/session")
    public void getSessionId(@Header(name = "simpSessionId") String sessionId) {
        System.out.println("sessionId : " + sessionId);
        System.out.println("Sending data from server");
        simpMessagingTemplate.convertAndSendToUser(sessionId, "/game/session", sessionId);
    }

    @MessageMapping("/game/create")
    @SendTo("/sub/game/create")
    public ResponseObject<GameDTO> createGame(@Header(name = "Authorization") String token, @Header(name = "clientId") String clientId,
                                              @Payload GameCreateDTO gameCreateDTO) {
        return gameService.createGame(token, gameCreateDTO);
    }

    @MessageMapping("/game/search")
    @SendTo("/sub/game/search")
    public ResponseObject<List<GameDTO>> searchGames() {
        return gameService.searchGames();
    }

    @MessageMapping("/game/enter")
    public ResponseObject<GameDTO> enterGame(@Header(name = "Authorization") String token, //@Header(name = "id") String clientId,
                          @Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.enterGame(token, gameId);

        simpMessagingTemplate.convertAndSend("/sub/game/enter", response);
        simpMessagingTemplate.convertAndSend("/sub/game/data/" + gameId, response);
        return gameService.enterGame(token, gameId);
    }

    @MessageMapping("/game/refresh")
    public void refreshGame(@Header(name = "gameId") Long gameId) {
//        try {
//            ResponseObject<GameDTO> response = gameService.refreshGame(gameId);
//            sessionMap.forEach((key, value) -> {
//                if(value.equals(gameId)) {
//                    simpMessagingTemplate.convertAndSendToUser(key, "/game/data", response);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ResponseObject<GameDTO> response = gameService.refreshGame(gameId);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }

    @MessageMapping("/game/exit")
    public void exitGame(@Header(name = "Authorization") String token, @Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.exitGame(token, gameId);

        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
        simpMessagingTemplate.convertAndSend("/sub/game/exit", response);
    }

    @MessageMapping("/game/chat")
    public void chat(@Header("Authorization") String token, @Header(name = "gameId") Long gameId,
                     @Payload GameChatDTO chatMessage) {

        System.out.println(token);

        try {
            GameChatDTO chatData = gameService.chat(token, chatMessage);
            if(chatData == null)
                throw new NullPointerException();

            String chatDestination = "/sub/game/chat/" + gameId;
            simpMessagingTemplate.convertAndSend(chatDestination, chatData);

//            sessionMap.forEach((key, value) -> {
//                if(value.equals(gameId)) {
//                    simpMessagingTemplate.convertAndSendToUser(key, "/game/chat", chatData);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}