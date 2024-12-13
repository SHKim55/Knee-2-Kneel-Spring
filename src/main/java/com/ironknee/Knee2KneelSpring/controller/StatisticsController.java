package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.statistics.GameResultDTO;
import com.ironknee.Knee2KneelSpring.dto.statistics.StatisticsDTO;
import com.ironknee.Knee2KneelSpring.service.statistics.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/read/user")
    public ResponseObject<StatisticsDTO> getStatisticsByUser(@RequestHeader(name = "Authorization") String token) {
        return statisticsService.getUserStatisticsByUser(token);
    }

    @GetMapping("/read/id")
    public ResponseObject<StatisticsDTO> getStatisticsById(@RequestBody Long statisticsId) {
        return statisticsService.getUserStatisticsById(statisticsId);
    }

    @PostMapping("/update")    // 게임 종료 시 통계치 업데이트
    public ResponseObject<Boolean> updateStatistics(@RequestBody Object gameResultDTO) {
        System.out.println(gameResultDTO.toString());
        return statisticsService.updateStatistics(gameResultDTO);
    }
}