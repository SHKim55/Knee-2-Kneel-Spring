package com.ironknee.Knee2KneelSpring.controller;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameChatDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import com.ironknee.Knee2KneelSpring.websocket.CustomChannelInterceptor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class WebSocketController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final CustomChannelInterceptor customChannelInterceptor;
    private final GameService gameService;

    public WebSocketController(final SimpMessageSendingOperations simpMessageSendingOperations,
                               final CustomChannelInterceptor customChannelInterceptor,
                               final GameService gameService) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
        this.customChannelInterceptor = customChannelInterceptor;
        this.gameService = gameService;
    }

//    private void sendMessageToGamePlayers(GameDTO gameDTO) {
//        System.out.println("game data in sendMessage : " + gameDTO.getPlayerList().toString());
//
//        for(Player player : gameDTO.getPlayerList()) {
//            String username = player.getUserEmail();
//            String sessionId = customChannelInterceptor.getSessionIdByUsername(username);
//
//            simpMessageSendingOperations.convertAndSendToUser(sessionId, "/sub/", gameDTO.toString());
//        }
//    }
//
//    private void sendMessageTest(GameDTO gameDTO) {
//        System.out.println("game data in sendMessage : " + gameDTO.getPlayerList().toString());
//
//        for(Player player : gameDTO.getPlayerList()) {
//            String username = player.getUserEmail();
//            String sessionId = customChannelInterceptor.getSessionIdByUsername(username);
//
//            simpMessageSendingOperations.convertAndSendToUser(sessionId, "/sub/", gameDTO.toString());
//        }
//    }

//    @MessageMapping("/create")
//    @SendTo("/sub/create")
//    public ResponseObject<GameCreateDTO> createGame(@Header("username") String token,
//                                                    @Payload GameCreateDTO gameCreateDTO) {
//        System.out.println("go in controller");
//        System.out.println("token : " + token);
//
//        String temporalToken = "test02@google.com";
//        System.out.println(temporalToken);
//
//        ResponseObject<GameCreateDTO> responseObject = new ResponseObject<>(ResponseCode.success.toString(), "sucess", gameCreateDTO);
//
//        System.out.println("maxPlayer : " + responseObject.getData().getMaxPlayer());
//        return responseObject;
////        sendMessageToGamePlayers(responseObject.getData());
//    }

    @MessageMapping("/game/enter")
    @SendTo("/sub/game/enter")
    public ResponseObject<GameDTO> enterGame(@Header(name = "Authorization") String token,
                                              @Header(name = "gameId") Long gameId) {
        return gameService.enterGame(token, gameId);
    }

    @MessageMapping("/game/refresh")
    @SendTo("/sub/game/data")
    public ResponseObject<GameDTO> refreshGame(@Header(name = "gameId") Long gameId) {
        return gameService.refreshGame(gameId);
    }

    @MessageMapping("/game/chat/{gameId}")
    @SendTo("/sub/game/chat/{gameId}")
    public GameChatDTO chat(@Header("Authorization") String token,
                            @Payload GameChatDTO chatMessage) {

        System.out.println(token);

        try {
            GameChatDTO data = gameService.chat(token, chatMessage);
            if(data == null)
                throw new NullPointerException();

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}