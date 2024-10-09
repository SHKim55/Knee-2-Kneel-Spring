package com.ironknee.Knee2KneelSpring.repository;

import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findUserEntityByUserId(UUID userId);
    Optional<List<UserEntity>> findUserEntitiesByUserId(UUID userId);
    Optional<UserEntity> findUserEntityByEmail(String email);

    @Override
    <S extends UserEntity> S save(S entity);

    @Override
    void delete(UserEntity entity);

    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
}
