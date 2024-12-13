package com.ironknee.Knee2KneelSpring.dto.player;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateDTO {
    private Long skillNum;
    private String skillName;
}
