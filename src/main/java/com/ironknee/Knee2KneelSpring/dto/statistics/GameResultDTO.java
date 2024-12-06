package com.ironknee.Knee2KneelSpring.dto.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class GameResultDTO {
    private List<UUID> playerIdList;
}
