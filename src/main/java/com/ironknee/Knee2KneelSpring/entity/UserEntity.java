package com.ironknee.Knee2KneelSpring.entity;

import com.ironknee.Knee2KneelSpring.service.user.MatchStatus;
import com.ironknee.Knee2KneelSpring.service.user.UserRank;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MatchStatus matchStatus = MatchStatus.none;

    @Column(nullable = false)
    @Builder.Default
    private Long exp = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long level = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long rankPoint = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRank userRank = UserRank.unranked;


    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<SkillEntity> skills;

    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<CharacterEntity> characters;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private StatisticsEntity statistics;
}
