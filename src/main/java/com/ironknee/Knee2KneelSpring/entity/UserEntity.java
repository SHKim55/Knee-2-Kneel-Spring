package com.ironknee.Knee2KneelSpring.entity;

import com.ironknee.Knee2KneelSpring.service.user.MatchStatus;
import com.ironknee.Knee2KneelSpring.service.user.UserRank;
import jakarta.persistence.*;
import lombok.*;

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
    private MatchStatus matchStatus = MatchStatus.none;

    @Column(nullable = false)
    private Long exp = 0L;

    @Column(nullable = false)
    private Long level = 0L;

    @Column(nullable = false)
    private Long rankPoint = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRank userRank = UserRank.unranked;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlayerEntity player;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private StatisticsEntity statistics;
}
