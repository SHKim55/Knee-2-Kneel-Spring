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
import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final StatisticsRepository statisticsRepository;

    public UserService(final PasswordEncoder passwordEncoder, final AuthenticationManager authenticationManager,
                       final JwtUtil jwtUtil, final UserRepository userRepository,
                       final StatisticsRepository statisticsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

        String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .nickname(userRegisterDTO.getNickname())
                .password(encodedPassword)
                .email(userRegisterDTO.getEmail())
                .build();

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
    public ResponseObject<String> logIn(UserLoginDTO userLoginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    // Email을 username으로 사용
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return new ResponseObject<>(ResponseCode.success.toString(), "success", jwt);

        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while logging in", null);
        }
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
