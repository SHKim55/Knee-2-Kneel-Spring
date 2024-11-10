package com.ironknee.Knee2KneelSpring.dto.player;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private Long id;
    private Long skillId;
    private String skillName;
}
