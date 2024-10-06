package com.ironknee.Knee2KneelSpring.entity;

import com.ironknee.Knee2KneelSpring.service.game.GameMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false)
    private LocalDateTime duration;

    @Column(nullable = false)
    private Long maxStudent;

    @Column(nullable = false)
    private Long maxAssistant;

    @Column(nullable = false)
    private Long maxCom;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameMode mode;

    @ManyToOne
    @JoinColumn(name = "stageId", nullable = false)
    private StageEntity stage;

    @Column(nullable = true)
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> playerEntityList;
}
