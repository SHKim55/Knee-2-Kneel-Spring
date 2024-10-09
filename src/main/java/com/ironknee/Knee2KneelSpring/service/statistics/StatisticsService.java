package com.ironknee.Knee2KneelSpring.service.statistics;

import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.statistics.StatisticsDTO;
import com.ironknee.Knee2KneelSpring.entity.StatisticsEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.StatisticsRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class StatisticsService {
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;

    public StatisticsService(final UserRepository userRepository, final StatisticsRepository statisticsRepository) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public static StatisticsDTO convertEntityToDTO(final StatisticsEntity statisticsEntity) {
        return StatisticsDTO.builder()
                .statisticsId(statisticsEntity.getStatisticsId())
                .roundTotal(statisticsEntity.getRoundTotal())
                .winTotal(statisticsEntity.getWinTotal())
                .loseTotal(statisticsEntity.getLoseTotal())
                .winRateTotal(statisticsEntity.getWinRateTotal())
                .roundStudent(statisticsEntity.getRoundStudent())
                .winStudent(statisticsEntity.getWinStudent())
                .loseStudent(statisticsEntity.getLoseStudent())
                .winRateStudent(statisticsEntity.getWinRateStudent())
                .roundProfessor(statisticsEntity.getRoundProfessor())
                .winProfessor(statisticsEntity.getWinProfessor())
                .loseProfessor(statisticsEntity.getLoseProfessor())
                .winRateProfessor(statisticsEntity.getWinRateProfessor())
                .roundAssistant(statisticsEntity.getRoundAssistant())
                .winAssistant(statisticsEntity.getWinAssistant())
                .loseAssistant(statisticsEntity.getLoseAssistant())
                .winRateAssistant(statisticsEntity.getWinRateAssistant())
                .build();
    }

    public ResponseObject<StatisticsDTO> getUserStatisticsByUser(final UUID userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(userId);

        if(optionalUserEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such id", null);
        }

        Optional<StatisticsEntity> optionalStatisticsEntity = statisticsRepository.findStatisticsEntityByUser(optionalUserEntity.get());

        if(optionalStatisticsEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No statistics data of this user", null);
        }
        else {
            StatisticsEntity statisticsEntity = optionalStatisticsEntity.get();
            StatisticsDTO statisticsDTO = convertEntityToDTO(statisticsEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", statisticsDTO);
        }
    }

    public ResponseObject<StatisticsDTO> getUserStatisticsById(final Long statisticsId) {
        Optional<StatisticsEntity> optionalStatisticsEntity = statisticsRepository.findStatisticsEntityByStatisticsId(statisticsId);

        if(optionalStatisticsEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No statistics data of this user", null);
        }
        else {
            StatisticsEntity statisticsEntity = optionalStatisticsEntity.get();
            StatisticsDTO statisticsDTO = convertEntityToDTO(statisticsEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", statisticsDTO);
        }
    }
}