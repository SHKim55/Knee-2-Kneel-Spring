package com.ironknee.Knee2KneelSpring.dto.game;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDTO {
    private Long maxPlayer;
    private Long maxStudent;
    private Long maxAssistant;

//    private StageEntity stageEntity;
}
