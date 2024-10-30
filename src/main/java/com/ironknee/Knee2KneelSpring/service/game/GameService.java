package com.ironknee.Knee2KneelSpring.service.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.StatisticsRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import com.ironknee.Knee2KneelSpring.service.player.Player;
import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
import com.ironknee.Knee2KneelSpring.service.user.MatchStatus;
import com.ironknee.Knee2KneelSpring.service.user.UserService;
import com.ironknee.Knee2KneelSpring.websocket.GameWebSocketHandler;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private final GameWebSocketHandler gameWebSocketHandler;
    private final JwtUtil jwtUtil;

    private final ArrayList<Game> gameList = new ArrayList<>();

    public GameService(final UserRepository userRepository, final StatisticsRepository statisticsRepository,
                       final GameWebSocketHandler gameWebSocketHandler, final JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.jwtUtil = jwtUtil;
    }

    // * Private Methods *
    private Long generateGameId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    private String convertToJson(GameDTO gameDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(gameDTO);
    }

    private UserEntity findUserByToken(String token) {
        Claims claims = jwtUtil.extractTokenValue(token);
        String email = claims.get("sub", String.class); // username(email) 추출

        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(email);
        return optionalUserEntity.orElse(null);
    }

    private Map<String, Object> findGameByGameId(Long gameId) {
        Game currentGame = null;
        int currentIndex = 0;
        for(int i = 0; i < gameList.size(); i++) {
            if(gameList.get(i).getGameId().equals(gameId)) {
                currentGame = gameList.get(i);
                currentIndex = i;
            }
        }

        if(currentGame == null) {
            return null;
        } else {
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("index", currentIndex);
            gameMap.put("game", currentGame);
            return gameMap;
        }
    }

    private Map<String, Object> findPlayerByPlayerId(UUID playerId, List<Player> playerList) {
        Player currentPlayer = null;
        int currentPlayerIndex = 0;
        for(int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getUserId() == playerId) {
                currentPlayer = playerList.get(i);
                currentPlayerIndex = i;
                break;
            }
        }

        if(currentPlayer == null) {
            return null;
        } else {
            Map<String, Object> playerMap = new HashMap<>();
            playerMap.put("index", currentPlayerIndex);
            playerMap.put("player", currentPlayer);
            return playerMap;
        }
    }

    private Game createGameObject(final Long gameId, final GameCreateDTO gameCreateDTO, final Player adminPlayer) {
        Game newGame = Game.builder()
                .gameId(gameId)
                .maxPlayer(gameCreateDTO.getMaxPlayer())
                .maxStudent(gameCreateDTO.getMaxStudent())
                .maxAssistant(gameCreateDTO.getMaxAssistant())
            //  .stageEntity(gameCreateDTO.getStageEntity())
//                .studentList(new ArrayList<>())
//                .assistantList(new ArrayList<>())
                .playerList(new ArrayList<>())
                .isPlaying(false)
                .build();

        newGame.getPlayerList().add(adminPlayer);
        return newGame;
    }

    // * Static Methods
    public static GameDTO convertEntityToDTO(final Game game) {
        return GameDTO.builder()
                .gameId(game.getGameId())
                .maxPlayer(game.getMaxPlayer())
                .maxStudent(game.getMaxStudent())
                .maxAssistant(game.getMaxAssistant())
//                .professor(game.getProfessor())
//                .studentList(game.getStudentList())
//                .assistantList(game.getAssistantList())
                .playerList(game.getPlayerList())
                .isPlaying(game.getIsPlaying())
                .build();
    }

    // * Public Member Methods *
    // 게임방 생성
    @Transactional
    public ResponseObject<GameDTO> createGame(final String token, final GameCreateDTO gameCreateDTO) {
        UserEntity userEntity;
        Player adminPlayer;
        Game newGame;

        try {
            userEntity = findUserByToken(token);

            adminPlayer = Player.builder()
                    .userId(userEntity.getUserId())
                    .userEmail(userEntity.getEmail())
                    .playerRole(Player.getRandomRole())
                    .isAdmin(true)
                    .isReady(false)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Error occurred in adding admin player", null);
        }

        try {
            Long gameId = generateGameId();
            newGame = createGameObject(gameId, gameCreateDTO, adminPlayer);
            gameList.add(newGame);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Error occurred in creating game room", null);
        }

        GameDTO newGameDTO = convertEntityToDTO(newGame);

        try {
            gameWebSocketHandler.sendMessageToClient(adminPlayer.getUserEmail(), convertToJson(newGameDTO));
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        userEntity.setMatchStatus(MatchStatus.matched);
        userRepository.save(userEntity);
        return new ResponseObject<>(ResponseCode.success.toString(), "success", newGameDTO);
    }

    // 게임방 검색
    public ResponseObject<List<GameDTO>> searchGames() {
        ArrayList<GameDTO> gameDTOList = new ArrayList<>();
        try {
            for(Game game : gameList)
                gameDTOList.add(convertEntityToDTO(game));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Error occurred while getting game list", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", gameDTOList);
    }

    // 게임방 입장
    @Transactional
    public ResponseObject<GameDTO> enterGame(final String token, final Long gameId) {
        UserEntity userEntity = findUserByToken(token);
        UserDTO userDTO = UserService.convertEntityToDTO(userEntity);
        Player currentPlayer = Player.builder()
                .userId(userDTO.getUserId())
                .userEmail(userDTO.getEmail())
                .playerRole(Player.getRandomRole())
                .isAdmin(false)
                .isReady(false)
                .build();

        if(userEntity.getMatchStatus() != MatchStatus.none) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The player is matched with another game or still playing", null);
        }

        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game gameToEnter = (Game) gameMap.get("game");
        int gameIndex = (int) gameMap.get("index");

        if(gameToEnter == null) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No such game room", null);
        } else if(gameToEnter.getIsPlaying()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);
        } else if(gameToEnter.getPlayerList().size() >= gameToEnter.getMaxPlayer()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already full", null);
        }

        List<Player> currentPlayerList = gameToEnter.getPlayerList();
        currentPlayerList.add(currentPlayer);
        gameToEnter.setPlayerList(currentPlayerList);
        gameList.set(gameIndex, gameToEnter);

        GameDTO newGameDTO = convertEntityToDTO(gameToEnter);

        try {
            for(Player player : currentPlayerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(newGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        userEntity.setMatchStatus(MatchStatus.matched);
        userRepository.save(userEntity);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", newGameDTO);
    }

    // 방장 변경
    @Transactional
    public ResponseObject<GameDTO> changeAdmin(Long gameId, UUID userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(userId);
        if(optionalUserEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such user id", null);
        }

        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentIndex = (int) gameMap.get("index");

        if(currentGame == null) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No game having such game id", null);
        }

        List<Player> playerList = currentGame.getPlayerList();
        Player previousAdmin = null, newAdmin = null;
        int previousAdminIndex = 0, newAdminIndex = 0;
        for(int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getIsAdmin()) {
                previousAdmin = playerList.get(i);
                previousAdminIndex = i;
            } else if(playerList.get(i).getUserId() == userId) {
                newAdmin = playerList.get(i);
                newAdminIndex = i;
            }
        }

        if(previousAdmin == null || newAdmin == null) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No player having such user id in this game object", null);
        }

        previousAdmin.setIsAdmin(false);
        newAdmin.setIsReady(true);
        playerList.set(previousAdminIndex, previousAdmin);
        playerList.set(newAdminIndex, newAdmin);

        currentGame.setPlayerList(playerList);
        gameList.set(currentIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        try {
            for(Player player : playerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 역할 변경
    public ResponseObject<GameDTO> changeRole(String token, Long gameId, PlayerRole playerRoleToChange) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        List<Player> playerList = currentGame.getPlayerList();

        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        Player currentPlayer = (Player) playerMap.get("player");
        int currentPlayerIndex = (int) playerMap.get("index");

        if(currentPlayer == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No player having such player id in game", null);

        currentPlayer.setPlayerRole(playerRoleToChange);
        playerList.set(currentPlayerIndex, currentPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        try {
            for(Player player : playerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        return new ResponseObject<>(ResponseCode.fail.toString(), "success", currentGameDTO);
    }

    // AI 플레이어 변경 (추후 개발)


    // 플레이어 대기상태 변경
    public ResponseObject<GameDTO> changeReadiness(String token, Long gameId, Boolean isReady) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        List<Player> playerList = currentGame.getPlayerList();

        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        Player currentPlayer = (Player) playerMap.get("player");
        int currentPlayerIndex = (int) playerMap.get("index");

        if(currentPlayer == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : no player having such player id in game", null);

        currentPlayer.setIsReady(!isReady);
        playerList.set(currentPlayerIndex, currentPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        try {
            for(Player player : playerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        return new ResponseObject<>(ResponseCode.fail.toString(), "success", currentGameDTO);
    }

    // 플레이어 삭제 (나가기)
    @Transactional
    public ResponseObject<GameDTO> exitGame(String token, Long gameId) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        List<Player> playerList = currentGame.getPlayerList();
        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        int currentPlayerIndex = (int) playerMap.get("index");

        playerList.remove(currentPlayerIndex);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        // 게임방에 아무도 남아있지 않는 경우 (게임방 삭제)
        if(playerList.isEmpty()) {
            gameList.remove(currentGameIndex);
        } else {
            try {
                for(Player player : playerList)
                    gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
            }
        }

        userEntity.setMatchStatus(MatchStatus.none);
        userRepository.save(userEntity);

        return new ResponseObject<>(ResponseCode.fail.toString(), "success", currentGameDTO);
    }

    // 게임 시작
    @Transactional
    public ResponseObject<GameDTO> startGame(Long gameId) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");

        List<Player> playerList = currentGame.getPlayerList();

        // 게임 시작을 위한 조건 검사 (팀 구성, 플레이어 준비 여부)
        int countProfessor = 0, countStudent = 0, countAssistant = 0;
        Boolean isNotReady = false;
        Boolean hasTooManyProfessor = false, hasTooManyStudent = false, hasTooManyAssistant = false;
        for(Player player : playerList) {
            if(!player.getIsReady()) {
                isNotReady = true;
                break;
            }

            if(player.getPlayerRole() == PlayerRole.professor) {
                countProfessor++;
                if(countProfessor > 1) {
                    hasTooManyProfessor = true;
                    break;
                }
            } else if(player.getPlayerRole() == PlayerRole.student) {
                countStudent++;
                if(countStudent > currentGame.getMaxStudent()) {
                    hasTooManyStudent = true;
                    break;
                }
            } else if(player.getPlayerRole() == PlayerRole.assistant) {
                countAssistant++;
                if(countAssistant > currentGame.getMaxAssistant()) {
                    hasTooManyAssistant = true;
                    break;
                }
            }
        }

        if(isNotReady) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Not all players are ready", null);
        } else if(hasTooManyProfessor) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The number of professor should be 1", null);
        } else if(hasTooManyStudent) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The number of students should be " + currentGame.getMaxStudent(), null);
        } else if(hasTooManyAssistant) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The number of assistants should be " + currentGame.getMaxAssistant(), null);
        }

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        try {
            for(Player player : playerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        // 플레이어 상태 변경 - 게임중
        for(Player player : playerList) {
            UserEntity userEntity = findUserByToken(player.getUserEmail());
            userEntity.setMatchStatus(MatchStatus.playing);
            userRepository.save(userEntity);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 게임 종료 (종료 후 대기방으로 복귀)
    @Transactional
    public ResponseObject<List<GameResultDTO>> finishGame(Long gameId, GameDTO gameDTO) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");
        List<Player> playerList = currentGame.getPlayerList();

        for(Player player : playerList) {
            UserEntity userEntity = findUserByToken(player.getUserEmail());
            userEntity.setMatchStatus(MatchStatus.none);
            userRepository.save(userEntity);
        }

        List<GameResultDTO> gameResultDTOList = new ArrayList<>();
        // 게임 통계 처리 로직 추후 추가

        return new ResponseObject<>(ResponseCode.success.toString(), "success", gameResultDTOList);
    }

    // 대기방 새로고침
    public ResponseObject<GameDTO> refreshGame(Long gameId) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");

        List<Player> playerList = currentGame.getPlayerList();
        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        try {
            for(Player player : playerList)
                gameWebSocketHandler.sendMessageToClient(player.getUserEmail(), convertToJson(currentGameDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Communication Error : Error occurred in sending message to clients", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }
}
