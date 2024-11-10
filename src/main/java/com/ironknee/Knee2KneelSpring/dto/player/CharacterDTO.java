package com.ironknee.Knee2KneelSpring.dto.player;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {
    private Long characterId;
    private Long characterNum;
    private String characterName;
}
