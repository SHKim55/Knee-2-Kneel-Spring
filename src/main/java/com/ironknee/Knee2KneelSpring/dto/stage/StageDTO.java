package com.ironknee.Knee2KneelSpring.dto.stage;

import com.ironknee.Knee2KneelSpring.service.stage_unused.StageDepartment;
import com.ironknee.Knee2KneelSpring.service.stage_unused.StageDifficulty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StageDTO {
    private Long stageId;
    @Enumerated(EnumType.STRING)
    private StageDepartment department;
    @Enumerated(EnumType.STRING)
    private StageDifficulty difficulty;
}
