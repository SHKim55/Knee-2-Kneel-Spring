package com.ironknee.Knee2KneelSpring.dto.game;

import com.ironknee.Knee2KneelSpring.service.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long gameId;
    @Builder.Default
    private String roomName = "New Normal Game";
    @Builder.Default
    private String mapName = "Computer Science";
    private Long maxPlayer;
    @Builder.Default
    private Long difficulty = 1L;

    private List<Player> playerList;

    private Boolean isPlaying;


// unused field
//    private Player professor;
//    private Long maxStudent;
//    private Long maxAssistant;
//    private List<Player> studentList;
//    private List<Player> assistantList;
//    private StageEntity stageEntity;
}
