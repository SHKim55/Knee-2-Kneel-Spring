package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
import com.ironknee.Knee2KneelSpring.service.statistics.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    // 게임방 생성
    // 방장으로부터 유저 정보, 게임 세팅 정보를 받은 후 게임방 객체 반환 (게임방 자동 입장)
    @PostMapping("/create")
    public ResponseObject<GameDTO> createGame(@RequestHeader(name = "Authorization") String token, @RequestBody GameCreateDTO gameCreateDTO) {
        return gameService.createGame(token, gameCreateDTO);
    }

    // 게임방 검색
    // 현재 생성된 게임방 객체의 리스트 전달
    @GetMapping("/search")
    public ResponseObject<List<GameDTO>> searchGames() {
        return gameService.searchGames();
    }

    // 게임방 입장
    // 게임방 객체의 리스트 중 하나를 클릭하면 해당 방으로 입장
    @PostMapping("/enter/{gameId}")
    public ResponseObject<GameDTO> enterGame(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId) {
        return gameService.enterGame(token, gameId);
    }

    // 방장 권한 부여
    // 방 생성자가 아닌 다른 사용자에게 방장 권한 부여
    @PostMapping("/modify/{gameId}/admin")
    public ResponseObject<GameDTO> changeAdmin(@PathVariable Long gameId, @RequestBody UUID userId) {
        return gameService.changeAdmin(gameId, userId);
    }

    // 역할 변경
    // 플레이어가 랜덤하게 지정된 초기 역할에서 자신이 맡고자 하는 역할로 변경 (교수는 1명으로 제한)
    @PostMapping("/modify/{gameId}/role/{role}")
    public ResponseObject<GameDTO> changeRole(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                              @RequestParam PlayerRole role) {
        return gameService.changeRole(token, gameId, role);
    }

    // AI 플레이어 변경 (미완성)
    // 매칭되지 않은 플레이어를 AI 플레이어로 대체할 수 있는 기능
    @PostMapping("/modify/{gameId}/toComputer/{slotNum}")
    public ResponseObject<GameDTO> changeToCom(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                               @PathVariable Long slotNum) {
//        return gameService.changeToCom(token, gameId, slotNum);
        return null;
    }

    // 플레이어 대기완료 상태 부여
    // 각 플레이어의 대기완료 여부 변경
    @PostMapping("/modify/{gameId}/ready")
    public ResponseObject<GameDTO> changeReadiness(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                                   @RequestBody Boolean isReady) {
        return gameService.changeReadiness(token, gameId, isReady);
    }

    // 대기방 나가기
    // 플레이어가 대기방을 나가면 Player 리스트에서 명단 삭제
    @PostMapping("/exit/{gameId}")
    public ResponseObject<GameDTO> exitGame(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId) {
        return gameService.exitGame(token, gameId);
    }

    // 게임 시작
    // 방 내 플레이어들이 모두 대기완료 상태일 경우 전체 게임 정보를 클라이언트로 전달
    @PostMapping("/start/{gameId}")
    public ResponseObject<GameDTO> startGame(@PathVariable Long gameId) {
        return gameService.startGame(gameId);
    }

    // 게임 종료
    // 게임이 종료된 경우 해당 게임방 객체 삭제
    // 게임 종료 후 통계치 업데이트 부분은 StatisticsController로 이관
    @PostMapping("/finish/{gameId}")
    public ResponseObject<List<GameResultDTO>> finishGame(@PathVariable Long gameId, @RequestBody GameDTO gameDTO) {
        return gameService.finishGame(gameId, gameDTO);
    }

    // 게임방 상태 새로고침
    // 게임방 설정에 변화가 있거나 멤버 및 팀 구성에 변화가 있는 경우 대기방 상태 업데이트
    @PostMapping("/refresh/{gameId}")
    public ResponseObject<GameDTO> refreshGame(@PathVariable Long gameId) {
        return gameService.refreshGame(gameId);
    }
}
