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
    private Long maxPlayer;
    private Long maxStudent;
    private Long maxAssistant;

//    private Player professor;
//    private List<Player> studentList;
//    private List<Player> assistantList;
    private List<Player> playerList;

    private Boolean isPlaying;

//    private StageEntity stageEntity;
}
