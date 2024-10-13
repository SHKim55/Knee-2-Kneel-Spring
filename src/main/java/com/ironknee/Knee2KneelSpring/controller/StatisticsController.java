package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.StatisticsDTO;
import com.ironknee.Knee2KneelSpring.service.statistics.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/read/user")
    public ResponseObject<StatisticsDTO> getStatisticsByUser(@RequestBody UUID userId) {
        return statisticsService.getUserStatisticsByUser(userId);
    }

    @GetMapping("/read/id")
    public ResponseObject<StatisticsDTO> getStatisticsById(@RequestBody Long statisticsId) {
        return statisticsService.getUserStatisticsById(statisticsId);
    }

//    @PostMapping("/update")    // 게임 종료 시 통계치 업데이트
//    public ResponseObject<Boolean> updateStatistics(@RequestBody GameResultDTO gameResultDTO) {
//        return statisticsService.updateStatistics(gameResultDTO);
//    }
}