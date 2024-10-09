package com.ironknee.Knee2KneelSpring.service.user;

import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserLoginDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserRegisterDTO;
import com.ironknee.Knee2KneelSpring.entity.StatisticsEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.StatisticsRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;

    public UserService(final UserRepository userRepository, final StatisticsRepository statisticsRepository) {
        this.userRepository = userRepository;
        this.statisticsRepository = statisticsRepository;
    }

    public static UserDTO convertEntityToDTO(final UserEntity userEntity) {
        return UserDTO.builder()
                .userId(userEntity.getUserId())
                .nickname(userEntity.getNickname())
                .password(userEntity.getPassword())
                .email(userEntity.getEmail())
                .matchStatus(userEntity.getMatchStatus())
                .exp(userEntity.getExp())
                .level(userEntity.getLevel())
                .rankPoint(userEntity.getRankPoint())
                .userRank(userEntity.getUserRank())
                .build();
    }

    @Transactional
    public ResponseObject<UserDTO> register(UserRegisterDTO userRegisterDTO) {
        if(userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : User having this email already exists", null);
        }
        if(userRepository.existsByNickname(userRegisterDTO.getNickname())) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : User having this nickname already exists", null);
        }

        UserEntity userEntity = UserEntity.builder()
                .nickname(userRegisterDTO.getNickname())
                .password(userRegisterDTO.getPassword())
                .email(userRegisterDTO.getEmail())
                .build();

        System.out.println(userEntity.toString());

        UserEntity newUserEntity;
        try {
            newUserEntity = userRepository.save(userEntity);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error occurred creating new user account", null);
        }

        StatisticsEntity statisticsEntity = StatisticsEntity.builder()
                .user(newUserEntity)
                .build();
        try {
            statisticsRepository.save(statisticsEntity);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error occurred creating statistics data of new user", null);
        }

        return new ResponseObject<>(ResponseCode.success.toString(), "success", convertEntityToDTO(newUserEntity));
    }

    @Transactional
    public ResponseObject<UserDTO> logIn(UserLoginDTO userLoginDTO) {
        return new ResponseObject<>();
    }

    public ResponseObject<UserDTO> getUserInfo(final UUID userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(userId);

        if(optionalUserEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such id",
                    new UserDTO());
        }
        else {
            UserEntity userEntity = optionalUserEntity.get();
            UserDTO userDTO = convertEntityToDTO(userEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", userDTO);
        }
    }

    @Transactional
    public ResponseObject<Boolean> signOut(final UUID userId) {
        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByUserId(userId);

        if(optionalUserEntity.isEmpty()) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No user having such id", null);
        }
        else {
            UserEntity userEntity = optionalUserEntity.get();

            try {
                userRepository.delete(userEntity);
            } catch (Exception e) {
                return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error deleting user info", null);
            }

            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        }
    }
}
