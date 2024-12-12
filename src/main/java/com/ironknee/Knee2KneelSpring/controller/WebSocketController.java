package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameChatDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
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

    @MessageMapping("/game/create")
    public void createGame(@Header(name = "Authorization") String token, @Header(name = "clientId") String clientId,
                                              @Payload GameCreateDTO gameCreateDTO) {
        ResponseObject<GameDTO> response = gameService.createGame(token, gameCreateDTO);
        sessionMap.put(clientId, response.getData().getGameId());

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("clientId", clientId);

        System.out.println(response.getData().getGameId());
        searchGames();
        simpMessagingTemplate.convertAndSend("/sub/game/create", response);
    }

    // 게임방 검색
    @MessageMapping("/game/search")
    @SendTo("/sub/game/search")
    public ResponseObject<List<GameDTO>> searchGames() {
        System.out.println("Calling searchGames");
        return gameService.searchGames();
    }

    // 게임방 생성
    @MessageMapping("/game/enter")
    public ResponseObject<GameDTO> enterGame(@Header(name = "Authorization") String token, //@Header(name = "id") String clientId,
                          @Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.enterGame(token, gameId);

        searchGames();
        simpMessagingTemplate.convertAndSend("/sub/game/enter", response);
        simpMessagingTemplate.convertAndSend("/sub/game/data/" + gameId, response);
        return gameService.enterGame(token, gameId);
    }

    // 게임방 업데이트
    @MessageMapping("/game/refresh")
    public void refreshGame(@Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.refreshGame(gameId);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }

    // 게임방 나가기
    @MessageMapping("/game/exit")
    public void exitGame(@Header(name = "Authorization") String token, @Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.exitGame(token, gameId);

        String dataDestination = "/sub/game/data/" + gameId;
        String exitDestination = "/sub/game/exit/" + gameId;
        if(!(response.getData() == null)) {   // 방이 사람이 남아있는 경우
            simpMessagingTemplate.convertAndSend(dataDestination, response);
            simpMessagingTemplate.convertAndSend(exitDestination, response);
        }
        else {
            simpMessagingTemplate.convertAndSend(exitDestination, response);
        }
        searchGames();
    }

    // 채팅 메시지 보내기
    @MessageMapping("/game/chat")
    public void chat(@Header("Authorization") String token, @Header(name = "gameId") Long gameId,
                     @Payload String chatMessage) {

        System.out.println(token);

        try {
            String chatData = gameService.chat(token, chatMessage);
            if(chatData == null)
                throw new NullPointerException();

            String chatDestination = "/sub/game/chat/" + gameId;
            simpMessagingTemplate.convertAndSend(chatDestination, chatData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 역할 변경
    // 플레이어가 랜덤하게 지정된 초기 역할에서 자신이 맡고자 하는 역할로 변경 (교수는 1명으로 제한)
    @MessageMapping("/game/modify/role")
    public void changeRole(@Header(name = "Authorization") String token, @Header(name = "gameId") Long gameId,
                           @Header(name = "playerRole") PlayerRole role, @Header(name = "nickname") String nickname) {
        ResponseObject<GameDTO> response = gameService.changeRole(token, gameId, role, nickname);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }

    // 플레이어 대기완료 상태 부여
    // 각 플레이어의 대기완료 여부 변경
    @MessageMapping("/game/modify/ready")
    public void changeReadiness(
            @Header(name = "Authorization") String token,
            @Header(name = "gameId") Long gameId) {
        ResponseObject<GameDTO> response = gameService.changeReadiness(token, gameId);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }

    // 게임 맵 변경
    // 플레이어들이 게임을 진행할 맵 변경
    @MessageMapping("/game/modify/map")
    public void changeMap(@Header(name = "gameId") Long gameId, @Header(name = "mapName") String mapName) {
        ResponseObject<GameDTO> response = gameService.changeMap(gameId, mapName);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }

    // 최대 인원 수 변경
    // 게임 최대 인원 수 확대 or 축소 (축소는 현재 인원수보다 크거나 같은 범위만 가능)
    @MessageMapping("/game/modify/maxPlayer")
    public void changeMaxPlayer(@Header(name = "gameId") Long gameId, @Header(name = "maxPlayer") Long maxPlayer) {
        ResponseObject<GameDTO> response = gameService.changeMaxPlayer(gameId, maxPlayer);
        String dataDestination = "/sub/game/data/" + gameId;
        simpMessagingTemplate.convertAndSend(dataDestination, response);
    }
}