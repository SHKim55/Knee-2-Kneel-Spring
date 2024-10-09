package com.ironknee.Knee2KneelSpring.repository;

import com.ironknee.Knee2KneelSpring.entity.StatisticsEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Long> {
    @Override
    <S extends StatisticsEntity> S save(S entity);

    @Override
    void delete(StatisticsEntity entity);

    Optional<StatisticsEntity> findStatisticsEntityByStatisticsId(Long statisticsId);

    Optional<StatisticsEntity> findStatisticsEntityByUser(UserEntity user);
}
