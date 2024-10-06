package com.ironknee.Knee2KneelSpring.entity;

import com.ironknee.Knee2KneelSpring.service.player.PlayerRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player")
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    @Column(nullable = false)
    private PlayerRole role;


    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "gameId", nullable = false)
    private GameEntity game;

//    @Column(nullable = true)
//    private List<SkillEntity> skillEntityList;
//
//    @Column(nullable = true)
//    private List<ItemEntity> itemEntityList;

}
