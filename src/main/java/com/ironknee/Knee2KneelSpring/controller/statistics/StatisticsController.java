package com.ironknee.Knee2KneelSpring.controller.statistics;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.statistics.StatisticsDTO;
import com.ironknee.Knee2KneelSpring.service.statistics.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/user")
    public ResponseObject<StatisticsDTO> getStatisticsByUser(@RequestBody UUID userId) {
        return statisticsService.getUserStatisticsByUser(userId);
    }

    @GetMapping("/id")
    public ResponseObject<StatisticsDTO> getStatisticsById(@RequestBody Long statisticsId) {
        return statisticsService.getUserStatisticsById(statisticsId);
    }
}