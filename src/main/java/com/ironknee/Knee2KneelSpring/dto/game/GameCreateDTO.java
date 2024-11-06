package com.ironknee.Knee2KneelSpring.dto.game;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDTO {
    private String roomName;
    private String mapName;
    private Long maxPlayer;
}
