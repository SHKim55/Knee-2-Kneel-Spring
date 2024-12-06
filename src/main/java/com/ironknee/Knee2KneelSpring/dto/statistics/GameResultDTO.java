package com.ironknee.Knee2KneelSpring.dto.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class GameResultDTO {
    private List<UUID> playerIdList;
}
