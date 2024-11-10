package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseObject<GameDTO> changeAdmin(@PathVariable Long gameId, @RequestBody String userEmail) {
        return gameService.changeAdmin(gameId, userEmail);
    }

    // 역할 변경
    // 플레이어가 랜덤하게 지정된 초기 역할에서 자신이 맡고자 하는 역할로 변경 (교수는 1명으로 제한)
    @PostMapping("/modify/{gameId}/role/{role}/{nickname}")
    public ResponseObject<GameDTO> changeRole(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                              @PathVariable PlayerRole role, @PathVariable String nickname) {
        return gameService.changeRole(token, gameId, role, nickname);
    }

    // 스킬 변경
    // 플레이어가 선택하는 스킬 리스트 정보 저장
    @PostMapping("/modify/{gameId}/skill")
    public ResponseObject<GameDTO> changeSkill(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                              @RequestBody List<Long> skillNumList) {
        return gameService.changeSkill(token, gameId, skillNumList);
    }

    // 캐릭터 변경
    // 플레이어가 선택하는 컨셉 캐릭터 정보 저장
    @PostMapping("/modify/{gameId}/character/{characterNum}")
    public ResponseObject<GameDTO> changeCharacter(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId,
                                               @PathVariable Long characterNum) {
        return gameService.changeCharacter(token, gameId, characterNum);
    }

    // AI 플레이어 생성
    // 대기방에 남은 자리가 있는 경우 AI 플레이어를 생성할 수 있는 기능
    @PostMapping("/modify/{gameId}/ai/create/{role}")
    public ResponseObject<GameDTO> createAI(@PathVariable Long gameId, @PathVariable PlayerRole role) {
        return gameService.createAI(gameId, role);
    }

    // AI 플레이어 삭제
    // 생성된 AI 플레이어를 다시 삭제할 수 있는 기능
    @PostMapping("/modify/{gameId}/ai/remove/{nickname}")
    public ResponseObject<GameDTO> createAI(@PathVariable Long gameId, @PathVariable String nickname) {
        return gameService.removeAI(gameId, nickname);
    }

    // 플레이어 대기완료 상태 부여
    // 각 플레이어의 대기완료 여부 변경
    @PostMapping("/modify/{gameId}/ready")
    public ResponseObject<GameDTO> changeReadiness(@RequestHeader(name = "Authorization") String token, @PathVariable Long gameId) {
        return gameService.changeReadiness(token, gameId);
    }

    // 게임 맵 변경
    // 플레이어들이 게임을 진행할 맵 변경
    @PostMapping("/modify/{gameId}/map/{mapName}")
    public ResponseObject<GameDTO> changeMap(@PathVariable(name = "gameId") Long gameId, @PathVariable(name = "mapName") String mapName) {
        return gameService.changeMap(gameId, mapName);
    }

    // 게임 난이도 변경
    // 하(1), 중(2), 상(3) 중 하나로 게임 난이도 설정
    @PostMapping("/modify/{gameId}/difficulty/{difficulty}")
    public ResponseObject<GameDTO> changeDifficulty(@PathVariable(name = "gameId") Long gameId, @PathVariable(name = "difficulty") Long difficulty) {
        return gameService.changeDifficulty(gameId, difficulty);
    }

    // 최대 인원 수 변경
    // 게임 최대 인원 수 확대 or 축소 (축소는 현재 인원수보다 크거나 같은 범위만 가능)
    @PostMapping("/modify/{gameId}/maxPlayer/{maxPlayer}")
    public ResponseObject<GameDTO> changeMaxPlayer(@PathVariable(name = "gameId") Long gameId,
                                                   @PathVariable(name = "maxPlayer") Long maxPlayer) {
        return gameService.changeMaxPlayer(gameId, maxPlayer);
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
