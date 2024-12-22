package com.ironknee.Knee2KneelSpring.service.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.game.GameChatDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.game.GameDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.entity.CharacterEntity;
import com.ironknee.Knee2KneelSpring.entity.SkillEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.CharacterRepository;
import com.ironknee.Knee2KneelSpring.repository.SkillRepository;
import com.ironknee.Knee2KneelSpring.repository.StatisticsRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import com.ironknee.Knee2KneelSpring.service.player.Player;
import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
import com.ironknee.Knee2KneelSpring.service.user.MatchStatus;
import com.ironknee.Knee2KneelSpring.service.user.UserRank;
import com.ironknee.Knee2KneelSpring.service.user.UserService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;
    private final SkillRepository skillRepository;
    private final CharacterRepository characterRepository;
    private final JwtUtil jwtUtil;

    private final ArrayList<Game> gameList = new ArrayList<>();
    private Long gameIdCounter = 1L;

    private Long aiPlayerNum = 0L;

    public GameService(final UserRepository userRepository, final StatisticsRepository statisticsRepository,
                       final SkillRepository skillRepository, final CharacterRepository characterRepository,
                       final JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
        this.skillRepository = skillRepository;
        this.characterRepository = characterRepository;
        this.jwtUtil = jwtUtil;
    }

    // * Private Methods *
    private Long generateGameId() {
//        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Long id = gameIdCounter;
        if(id < 100)
            gameIdCounter += 1;
        else
            gameIdCounter = (gameIdCounter % 100) + 1;

        return id;
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
            if(playerList.get(i).getUserId().equals(playerId)) {
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
                .roomName(gameCreateDTO.getRoomName())
                .mapName(gameCreateDTO.getMapName())
                .maxPlayer(gameCreateDTO.getMaxPlayer())
                .difficulty(1L)  // Default 난이도 (하)
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
                .roomName(game.getRoomName())
                .mapName(game.getMapName())
                .maxPlayer(game.getMaxPlayer())
                .difficulty(game.getDifficulty())
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
                    .userNickname(userEntity.getNickname())
                    .level(userEntity.getLevel())
                    .userRank(userEntity.getUserRank())
                    .rankPoint(userEntity.getRankPoint())
                    .playerRole(PlayerRole.professor)
                    .isAdmin(true)
                    .isReady(true)
                    .skillNumList(new ArrayList<>())   // 장착 스킬 없음
                    .characterNum(0L)                  // Default Character
                    .isAI(false)
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
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game room selected is already full", null);
        }

        Player currentPlayer = Player.builder()
                .userId(userDTO.getUserId())
                .userEmail(userDTO.getEmail())
                .userNickname(userDTO.getNickname())
                .level(userDTO.getLevel())
                .userRank(userDTO.getUserRank())
                .rankPoint(userDTO.getRankPoint())
                .playerRole(Player.grantPlayerRole(gameToEnter))
                .isAdmin(false)
                .isReady(false)
                .skillNumList(new ArrayList<>())
                .characterNum(0L)
                .isAI(false)
                .build();

        List<Player> currentPlayerList = gameToEnter.getPlayerList();
        currentPlayerList.add(currentPlayer);
        gameToEnter.setPlayerList(currentPlayerList);
        gameList.set(gameIndex, gameToEnter);

        GameDTO newGameDTO = convertEntityToDTO(gameToEnter);

        userEntity.setMatchStatus(MatchStatus.matched);
        userRepository.save(userEntity);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", newGameDTO);
    }

    // 방장 변경
    @Transactional
    public ResponseObject<GameDTO> changeAdmin(Long gameId, String userEmail) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(userEmail);
        if(optionalUserEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such user id", null);
        }

        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentIndex = (int) gameMap.get("index");

        if(currentGame == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No game having such game id", null);
        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();
        Player previousAdmin = null, newAdmin = null;
        int previousAdminIndex = 0, newAdminIndex = 0;
        for(int i = 0; i < playerList.size(); i++) {
            if(playerList.get(i).getIsAdmin()) {
                previousAdmin = playerList.get(i);
                previousAdminIndex = i;
            } else if(playerList.get(i).getUserEmail().equals(userEmail)) {
                newAdmin = playerList.get(i);
                newAdminIndex = i;
            }
        }

        if(previousAdmin == null || newAdmin == null) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No player having such user id in this game object", null);
        }

        previousAdmin.setIsAdmin(false);
        newAdmin.setIsAdmin(true);
        playerList.set(previousAdminIndex, previousAdmin);
        playerList.set(newAdminIndex, newAdmin);

        currentGame.setPlayerList(playerList);
        gameList.set(currentIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    public ResponseObject<GameDTO> changeRole(String token, Long gameId, PlayerRole playerRoleToChange,
                                              String nickname) {
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");
        List<Player> playerList = currentGame.getPlayerList();

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        boolean hasProfessor = false;
        int studentNum = 0, assistantNum = 0;
        for(Player player : playerList) {
            if(player.getPlayerRole() == PlayerRole.professor)
                hasProfessor = true;
            else if(player.getPlayerRole() == PlayerRole.student)
                studentNum++;
            else if(player.getPlayerRole() == PlayerRole.assistant)
                assistantNum++;
        }

        if(studentNum >= 5 && playerRoleToChange == PlayerRole.student)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The student team is already full", null);
        if((assistantNum > 4 || (assistantNum == 4 && hasProfessor)) && playerRoleToChange == PlayerRole.assistant)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The LAB team is already full", null);

        UserEntity userEntity;
        Player currentPlayer;
        int currentPlayerIndex;
        if(!nickname.startsWith("ai_")) {   // 사람 플레이어
            userEntity = findUserByToken(token);
            Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
            currentPlayer = (Player) playerMap.get("player");
            currentPlayerIndex = (int) playerMap.get("index");

            if(currentPlayer == null)
                return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No player having such player id in game", null);

            currentPlayer.setPlayerRole(playerRoleToChange);
            playerList.set(currentPlayerIndex, currentPlayer);
        } else {  // AI 플레이어
            boolean isValidAINickname = false;
            for(Player aiPlayer : playerList) {
                if(aiPlayer.getUserNickname().equals(nickname)) {
                    aiPlayer.setPlayerRole(playerRoleToChange);
                    isValidAINickname = true;
                }
            }

            if(!isValidAINickname)
                return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No AI player having such nickname", null);
        }

        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 플레이어 스킬 리스트 변경
    public ResponseObject<GameDTO> changeSkill(String token, Long gameId, List<Long> skillNumList) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();

        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        Player currentPlayer = (Player) playerMap.get("player");
        int currentPlayerIndex = (int) playerMap.get("index");

        if(currentPlayer == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No player having such player id in game", null);

        try {
            if(skillNumList.size() > 3)
                throw new IndexOutOfBoundsException();

            for(Long skillNum : skillNumList) {
                Optional<SkillEntity> optionalSkillEntity = skillRepository.findSkillEntityBySkillNum(skillNum);
                if(optionalSkillEntity.isEmpty() || !userEntity.getSkills().contains(optionalSkillEntity.get()))
                    throw new Exception();
            }
        } catch (IndexOutOfBoundsException i) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : The length of skill list must be less or equal to 3", null);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Skill having such skill number doesn't exist or not able to access", null);
        }

        currentPlayer.setSkillNumList(skillNumList);
        playerList.set(currentPlayerIndex, currentPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 플레이어 컨셉 캐릭터 변경
    public ResponseObject<GameDTO> changeCharacter(String token, Long gameId, Long characterNum) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();

        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        Player currentPlayer = (Player) playerMap.get("player");
        int currentPlayerIndex = (int) playerMap.get("index");

        if(currentPlayer == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : No player having such player id in game", null);

        Optional<CharacterEntity> optionalCharacterEntity = characterRepository.findCharacterEntityByCharacterNum(characterNum);
        if(optionalCharacterEntity.isEmpty() || !userEntity.getCharacters().contains(optionalCharacterEntity.get()))
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Character having such character id doesn't exist or not able to access", null);

        currentPlayer.setCharacterNum(characterNum);
        playerList.set(currentPlayerIndex, currentPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // AI 플레이어 생성
    public ResponseObject<GameDTO> createAI(Long gameId, PlayerRole role) {
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();
        if(playerList.size() >= 10)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The waiting room is already full", null);

        int studentNum = 0, assistantNum = 0;
        for(Player player : playerList) {
            if(player.getPlayerRole() == PlayerRole.student)
                studentNum++;
            else
                assistantNum++;
        }

        Player aiPlayer;
        if(studentNum < 5 && role == PlayerRole.student) {
            aiPlayer = Player.builder()
                    .userId(UUID.randomUUID())
                    .userEmail("")
                    .userNickname("ai_player_" + aiPlayerNum)
                    .level(0L)
                    .userRank(UserRank.unranked)
                    .rankPoint(0L)
                    .playerRole(PlayerRole.student)
                    .isAdmin(false)
                    .isReady(true)
                    .skillNumList(new ArrayList<>())
                    .characterNum(0L)
                    .isAI(true)
                    .build();
        } else if(assistantNum < 5 && role == PlayerRole.assistant) {
            aiPlayer = Player.builder()
                    .userId(UUID.randomUUID())
                    .userEmail("")
                    .userNickname("ai_player_" + aiPlayerNum)
                    .level(0L)
                    .userRank(UserRank.unranked)
                    .rankPoint(0L)
                    .playerRole(PlayerRole.assistant)
                    .isAdmin(false)
                    .isReady(true)
                    .skillNumList(new ArrayList<>())
                    .characterNum(0L)
                    .isAI(true)
                    .build();
        } else {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The number of " + role.toString() + " player is already sufficient", null);
        }

        playerList.add(aiPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO newGameDTO = convertEntityToDTO(currentGame);

        aiPlayerNum++;
        return new ResponseObject<>(ResponseCode.success.toString(), "success", newGameDTO);
    }

    // AI 플레이어 삭제
    public ResponseObject<GameDTO> removeAI(Long gameId, String nickname) {
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();
        playerList.removeIf(player -> player.getUserNickname().equals(nickname));

        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO newGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", newGameDTO);
    }

    // 플레이어 대기상태 변경
    public ResponseObject<GameDTO> changeReadiness(String token, Long gameId) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);

        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();

        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        Player currentPlayer = (Player) playerMap.get("player");
        int currentPlayerIndex = (int) playerMap.get("index");

        if(currentPlayer == null)
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : no player having such player id in game", null);

        currentPlayer.setIsReady(!currentPlayer.getIsReady());
        playerList.set(currentPlayerIndex, currentPlayer);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 맵 변경
    public ResponseObject<GameDTO> changeMap(Long gameId, String mapName) {
        Game currentGame;
        List<Player> playerList;
        try {
            Map<String, Object> gameMap = findGameByGameId(gameId);

            currentGame = (Game) gameMap.get("game");
            playerList = currentGame.getPlayerList();
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : No game having such id", null);
        }

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        GameDTO currentGameDTO;
        try {
            currentGame.setMapName(mapName);
            currentGameDTO = convertEntityToDTO(currentGame);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Invalid map name", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 맵 변경
    public ResponseObject<GameDTO> changeDifficulty(Long gameId, Long difficulty) {
        Game currentGame;
        List<Player> playerList;
        try {
            Map<String, Object> gameMap = findGameByGameId(gameId);

            currentGame = (Game) gameMap.get("game");
            playerList = currentGame.getPlayerList();
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : No game having such id", null);
        }

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        GameDTO currentGameDTO;
        try {
            if(difficulty < 1 || difficulty > 3)
                throw new Exception();

            currentGame.setDifficulty(difficulty);
            currentGameDTO = convertEntityToDTO(currentGame);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Invalid difficulty value", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 최대 플레이어 수 변경
    public ResponseObject<GameDTO> changeMaxPlayer(Long gameId, Long maxPlayer) {
        Game currentGame;
        List<Player> playerList;
        try {
            Map<String, Object> gameMap = findGameByGameId(gameId);

            currentGame = (Game) gameMap.get("game");
            playerList = currentGame.getPlayerList();
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : No game having such id", null);
        }

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        GameDTO currentGameDTO;
        try {
            if(maxPlayer < playerList.size() || maxPlayer > 10)
                throw new Exception();

            currentGame.setMaxPlayer(maxPlayer);
            currentGameDTO = convertEntityToDTO(currentGame);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Invalid difficulty value", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 플레이어 삭제 (나가기)
    @Transactional
    public ResponseObject<GameDTO> exitGame(String token, Long gameId) {
        UserEntity userEntity = findUserByToken(token);
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");
        int currentGameIndex = (int) gameMap.get("index");

        if(currentGame.getIsPlaying())
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The game selected is already playing", null);

        List<Player> playerList = currentGame.getPlayerList();
        Map<String, Object> playerMap = findPlayerByPlayerId(userEntity.getUserId(), playerList);
        int currentPlayerIndex = (int) playerMap.get("index");

        playerList.remove(currentPlayerIndex);
        currentGame.setPlayerList(playerList);
        gameList.set(currentGameIndex, currentGame);

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        // 게임방에서 나간 유저 매칭 대기상태 변경
        userEntity.setMatchStatus(MatchStatus.none);
        userRepository.save(userEntity);

        // 게임방에 아무도 남아있지 않는 경우 (게임방 삭제)
        if(playerList.isEmpty()) {
            gameList.remove(currentGameIndex);
            return new ResponseObject<>(ResponseCode.success.toString(), "empty room has been deleted successfully", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 게임 시작
    @Transactional
    public ResponseObject<GameDTO> startGame(Long gameId) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");

        List<Player> playerList = currentGame.getPlayerList();

        // 게임 시작을 위한 조건 검사 (팀 구성, 플레이어 준비 여부)
        int countProfessor = 0;
        Boolean isNotReady = false;
        Boolean hasTooManyProfessor = false;

        if (playerList.size() > currentGame.getMaxPlayer()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : This game has too many players", null);
        }

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
            }
        }

        if(isNotReady) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : Not all players are ready", null);
        } else if(hasTooManyProfessor) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Error : The number of professor should be 1", null);
        }

        currentGame.setIsPlaying(true);
        GameDTO currentGameDTO = convertEntityToDTO(currentGame);

        // 플레이어 상태 변경 - 게임중
        for(Player player : playerList) {
            Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(player.getUserEmail());
            if(optionalUserEntity.isEmpty()) return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such email - " + player.getUserEmail(), null);

            UserEntity userEntity = optionalUserEntity.get();
            userEntity.setMatchStatus(MatchStatus.playing);
            userRepository.save(userEntity);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 게임 종료 (종료 후 대기방으로 복귀)
//    @Transactional
//    public ResponseObject<GameDTO> finishGame(Long gameId, GameResultDTO gameResultDTO) {
//        Map<String, Object> gameMap = findGameByGameId(gameId);
//        Game currentGame = (Game) gameMap.get("game");
//        int currentGameIndex = (int) gameMap.get("index");
//        List<Player> playerList = currentGame.getPlayerList();
//
//        playerList.removeIf(player -> !gameResultDTO.getPlayerIdList().contains(player.getUserId()));
//
//        for(Player player : playerList) {
//            Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(player.getUserId());
//            if(optionalUserEntity.isEmpty())
//                return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Invalid player information", null);
//
//            UserEntity userEntity = optionalUserEntity.get();
//            userEntity.setMatchStatus(MatchStatus.none);
//            userRepository.save(userEntity);
//        }
//
//        currentGame.setPlayerList(playerList);
//        gameList.set(currentGameIndex, currentGame);
//        GameDTO gameDTO = convertEntityToDTO(currentGame);
//
//        return new ResponseObject<>(ResponseCode.success.toString(), "success", gameDTO);
//    }

    // 게임 종료 (종료 후 로비로 복귀)
    @Transactional
    public ResponseObject<GameDTO> finishGame(Long gameId) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        int currentGameIndex = (int) gameMap.get("index");
        Game currentGame = (Game) gameMap.get("game");

        List<Player> currentPlayerList = currentGame.getPlayerList();
        for(Player player : currentPlayerList) {
            Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(player.getUserId());
            if(optionalUserEntity.isEmpty()) continue;

            UserEntity userEntity = optionalUserEntity.get();
            userEntity.setMatchStatus(MatchStatus.none);
            userRepository.save(userEntity);
        }

        gameList.remove(currentGameIndex);
        return new ResponseObject<>(ResponseCode.success.toString(), "success", null);
    }

    // 대기방 새로고침
    public ResponseObject<GameDTO> refreshGame(Long gameId) {
        Map<String, Object> gameMap = findGameByGameId(gameId);
        Game currentGame = (Game) gameMap.get("game");

        GameDTO currentGameDTO = convertEntityToDTO(currentGame);
        return new ResponseObject<>(ResponseCode.success.toString(), "success", currentGameDTO);
    }

    // 대기방 채팅
    public GameChatDTO chat(String token, GameChatDTO chatMessage) {
        UserEntity user = findUserByToken(token);
        if(user == null) return null;

//        return user.getNickname() + ": " + chatMessage;
//        return chatMessage.getNickname() + " : " + chatMessage.getMessage();

        return chatMessage;
    }

    public void initializeMatch(UserDTO userDTO) {
        for(int i = 0; i < gameList.size(); i++) {
            Game currentGame = gameList.get(i);
            List<Player> playerList = currentGame.getPlayerList();

            playerList.removeIf(player -> player.getUserId().equals(userDTO.getUserId()));

            boolean hasAdmin = false;
            // 남아있는 유저 중 가장 먼저 들어온 유저에게 방장 권한 위임
            if(!playerList.isEmpty()) {
                for(int j = 0; j < playerList.size(); j++) {
                    if(playerList.get(j).getIsAdmin()) {
                        hasAdmin = true;
                        break;
                    }
                }

                if(!hasAdmin) {
                    Player newAdminPlayer = playerList.get(0);
                    newAdminPlayer.setIsAdmin(true);
                    playerList.set(i, newAdminPlayer);
                }
            }

            currentGame.setPlayerList(playerList);
            gameList.set(i, currentGame);
        }

        gameList.removeIf((game -> game.getPlayerList().isEmpty()));
    }
}