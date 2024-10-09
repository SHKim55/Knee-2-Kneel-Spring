package com.ironknee.Knee2KneelSpring.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    private Long statisticsId;
    private Long roundTotal;
    private Long winTotal;
    private Long loseTotal;
    private Double winRateTotal;
    private Long roundStudent;
    private Long winStudent;
    private Long loseStudent;
    private Double winRateStudent;
    private Long roundProfessor;
    private Long winProfessor;
    private Long loseProfessor;
    private Double winRateProfessor;
    private Long roundAssistant;
    private Long winAssistant;
    private Long loseAssistant;
    private Double winRateAssistant;
}
