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
    @Builder.Default
    private String roomName = "New Normal Game";
    @Builder.Default
    private String mapName = "Computer Science";
    private Long maxPlayer;
    @Builder.Default
    private Long difficulty = 1L;

    private List<Player> playerList = new ArrayList<>();
    private Boolean isPlaying = false;
}
