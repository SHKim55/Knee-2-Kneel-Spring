package com.ironknee.Knee2KneelSpring.service.game;

import com.ironknee.Knee2KneelSpring.service.player.Player;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private Long gameId;
    private Long maxPlayer;
    private Long maxStudent;
    private Long maxAssistant;

//    private Player professor;
//    private List<Player> studentList = new ArrayList<>();
//    private List<Player> assistantList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();

    private Boolean isPlaying = false;

//    private StageEntity stageEntity;
}
