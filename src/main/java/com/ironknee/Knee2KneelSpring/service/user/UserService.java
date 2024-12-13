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
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

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

    private UserEntity findUserByToken(String token) {
        Claims claims = jwtUtil.extractTokenValue(token);
        String email = claims.get("sub", String.class); // username(email) 추출

        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(email);
        return optionalUserEntity.orElse(null);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false; // null 또는 빈 문자열은 유효하지 않음
        }

        // 이메일 정규식
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Pattern 클래스 사용
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
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

        if(!isValidEmail(userRegisterDTO.getEmail())) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : User having wrong email input", null);
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

    @Transactional
    public Map<String, Object> getUserInfo(final String token) {
        Map<String ,Object> hashMap = new HashMap<>();
        UserEntity userEntity = findUserByToken(token);

        if(userEntity == null) {
            hashMap.put("error", "DB Error : No user having such id");
            return hashMap;
        }

        // 매칭방 내 유저, 게임 내 유저의 재접속 시 매칭 상태 정보 초기화
        if(userEntity.getMatchStatus() == MatchStatus.matched ||
                userEntity.getMatchStatus() == MatchStatus.playing) {

            userEntity.setMatchStatus(MatchStatus.none);
            userRepository.save(userEntity);

            hashMap.put("reconnect", convertEntityToDTO(userEntity));
            return hashMap;
        }

        hashMap.put("new", convertEntityToDTO(userEntity));
        return hashMap;
    }

    @Transactional
    public ResponseObject<Boolean> signOut(final String token) {
        try {
            UserEntity userEntity = findUserByToken(token);

            if(userEntity == null) {
                return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No such user", null);
            }

            userRepository.delete(userEntity);
            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error deleting user info", null);
        }
    }
}
