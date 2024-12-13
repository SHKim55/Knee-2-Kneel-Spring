package com.ironknee.Knee2KneelSpring.repository;

import com.ironknee.Knee2KneelSpring.entity.SkillEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
    Optional<SkillEntity> findSkillEntityBySkillId(Long skillId);
    Optional<SkillEntity> findSkillEntityBySkillNum(Long skillNum);
    List<SkillEntity> findAllBySkillId(Long skillId);

    @Override
    <S extends SkillEntity> S save(S entity);

    @Override
    void delete(SkillEntity entity);

    Boolean existsBySkillId(Long skillId);
    Boolean existsBySkillNum(Long skillNum);
    Boolean existsBySkillName(String skillName);
}
