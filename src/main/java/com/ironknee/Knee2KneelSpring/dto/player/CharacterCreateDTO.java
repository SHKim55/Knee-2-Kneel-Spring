package com.ironknee.Knee2KneelSpring.dto.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCreateDTO {
    private Long characterNum;
    private String characterName;
}
