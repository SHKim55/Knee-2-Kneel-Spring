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
    private Long roundTotal = 0L;

    @Column(nullable = false)
    private Long winTotal = 0L;

    @Column(nullable = false)
    private Long loseTotal = 0L;

    @Column(nullable = false)
    private Long winRateTotal = 0L;

    @Column(nullable = false)
    private Long roundStudent = 0L;

    @Column(nullable = false)
    private Long winStudent = 0L;

    @Column(nullable = false)
    private Long loseStudent = 0L;

    @Column(nullable = false)
    private Long winRateStudent = 0L;

    @Column(nullable = false)
    private Long roundProfessor = 0L;

    @Column(nullable = false)
    private Long winProfessor = 0L;

    @Column(nullable = false)
    private Long loseProfessor = 0L;

    @Column(nullable = false)
    private Long winRateProfessor = 0L;

    @Column(nullable = false)
    private Long roundAssistant = 0L;

    @Column(nullable = false)
    private Long winAssistant = 0L;

    @Column(nullable = false)
    private Long loseAssistant = 0L;

    @Column(nullable = false)
    private Long winRateAssistant = 0L;


    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;
}
