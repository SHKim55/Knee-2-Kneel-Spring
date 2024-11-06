package com.ironknee.Knee2KneelSpring.entity;

import com.ironknee.Knee2KneelSpring.service.stage_unused.StageDepartment;
import com.ironknee.Knee2KneelSpring.service.stage_unused.StageDifficulty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stage")
public class StageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stageId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StageDepartment department;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StageDifficulty difficulty;

//    @Column(nullable = true)
//    private String assignment;

//    @Column(nullable = true)
//    private List<Mission> missionList;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameEntity> gameEntityList;
}
