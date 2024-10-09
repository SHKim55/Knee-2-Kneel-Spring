package com.ironknee.Knee2KneelSpring.repository;

import com.ironknee.Knee2KneelSpring.entity.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<GameEntity, Long> {

}
