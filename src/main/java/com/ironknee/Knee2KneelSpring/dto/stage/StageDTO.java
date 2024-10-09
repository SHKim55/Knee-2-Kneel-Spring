package com.ironknee.Knee2KneelSpring.dto.stage;

import com.ironknee.Knee2KneelSpring.entity.GameEntity;
import com.ironknee.Knee2KneelSpring.service.stage.StageDepartment;
import com.ironknee.Knee2KneelSpring.service.stage.StageDifficulty;
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
    private List<GameEntity> gameEntityList;
}
