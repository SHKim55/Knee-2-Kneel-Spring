package com.ironknee.Knee2KneelSpring.repository;

import com.ironknee.Knee2KneelSpring.entity.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, Long> {
    Optional<CharacterEntity> findCharacterEntityByCharacterId(Long id);
    Optional<CharacterEntity> findCharacterEntityByCharacterNum(Long characterId);
    List<CharacterEntity> findAllByCharacterId(Long id);

    @Override
    <S extends CharacterEntity> S save(S entity);

    @Override
    void delete(CharacterEntity entity);

    Boolean existsByCharacterId(Long characterId);
    Boolean existsByCharacterNum(Long characterNum);
    Boolean existsByCharacterName(String characterName);
}
