package com.ironknee.Knee2KneelSpring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "statistics")
public class StatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsId;

    @Column(nullable = false)
    @Builder.Default
    private Long roundTotal = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long winTotal = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long loseTotal = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double winRateTotal = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Long roundStudent = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long winStudent = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long loseStudent = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double winRateStudent = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Long roundProfessor = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long winProfessor = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long loseProfessor = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double winRateProfessor = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Long roundAssistant = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long winAssistant = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long loseAssistant = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Double winRateAssistant = 0.0;


    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;
}
