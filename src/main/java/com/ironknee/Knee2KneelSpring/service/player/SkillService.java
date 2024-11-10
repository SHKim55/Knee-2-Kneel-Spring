package com.ironknee.Knee2KneelSpring.service.player;

import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.player.SkillCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.player.SkillDTO;
import com.ironknee.Knee2KneelSpring.entity.SkillEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.SkillRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public SkillService(final SkillRepository skillRepository, final UserRepository userRepository,
                        final JwtUtil jwtUtil) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private UserEntity findUserByToken(String token) {
        Claims claims = jwtUtil.extractTokenValue(token);
        String email = claims.get("sub", String.class); // username(email) 추출

        Optional<UserEntity> optionalUserEntity = userRepository.findUserEntityByEmail(email);
        return optionalUserEntity.orElse(null);
    }

    // * Static Methods
    public static SkillDTO convertEntityToDTO(final SkillEntity skill) {
        return SkillDTO.builder()
                .id(skill.getSkillId())
                .skillId(skill.getSkillNum())
                .skillName(skill.getSkillName())
                .build();
    }

    public ResponseObject<SkillDTO> createSkill(final SkillCreateDTO skillCreateDTO) {
        try {
            SkillEntity skillEntity = SkillEntity.builder()
                    .skillNum(skillCreateDTO.getSkillNum())
                    .skillName(skillCreateDTO.getSkillName())
                    .build();

            skillEntity = skillRepository.save(skillEntity);
            return new ResponseObject<>(ResponseCode.success.toString(), "success", convertEntityToDTO(skillEntity));
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred in saving skill info", null);
        }
    }

    public ResponseObject<List<SkillDTO>> searchSkills() {
        try {
            List<SkillEntity> skillEntityList = skillRepository.findAll();

            List<SkillDTO> skillDTOList = new ArrayList<>();
            skillEntityList.forEach(skill -> skillDTOList.add(convertEntityToDTO(skill)));

            return new ResponseObject<>(ResponseCode.success.toString(), "success", skillDTOList);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while loading skill list", null);
        }
    }

    public ResponseObject<List<SkillDTO>> searchUserSkills(String token) {
        try {
            UserEntity userEntity = findUserByToken(token);

            List<SkillDTO> skillDTOList = new ArrayList<>();
            userEntity.getSkills().forEach(
                    skillEntity -> skillDTOList.add(convertEntityToDTO(skillEntity))
            );

            return new ResponseObject<>(ResponseCode.success.toString(), "success", skillDTOList);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while loading skill list for user", null);
        }
    }

    public ResponseObject<Boolean> grantSkill(final String token, final Long skillNum) {
        if(!skillRepository.existsBySkillNum(skillNum))
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No skill having such skill number", null);

        UserEntity userEntity = findUserByToken(token);
        SkillEntity skillEntity = skillRepository.findSkillEntityBySkillNum(skillNum).orElse(null);

        try {
            List<UserEntity> skillUsers = skillEntity.getUsers();
            List<SkillEntity> userSkills = userEntity.getSkills();
            skillUsers.add(userEntity);
            skillEntity.setUsers(skillUsers);
            userSkills.add(skillEntity);
            userEntity.setSkills(userSkills);

            skillRepository.save(skillEntity);
            userRepository.save(userEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while adding skill to user", null);
        }
    }

    public ResponseObject<Boolean> revokeSkill(final String token, final Long skillNum) {
        if(!skillRepository.existsBySkillNum(skillNum))
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No skill having such skill number", null);

        UserEntity userEntity = findUserByToken(token);
        SkillEntity skillEntity = skillRepository.findSkillEntityBySkillNum(skillNum).orElse(null);

        try {
            List<UserEntity> skillUsers = skillEntity.getUsers();
            List<SkillEntity> userSkills = userEntity.getSkills();
            skillUsers.remove(userEntity);
            skillEntity.setUsers(skillUsers);
            userSkills.remove(skillEntity);
            userEntity.setSkills(userSkills);

            skillRepository.save(skillEntity);
            userRepository.save(userEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while removing skill to user", null);
        }
    }

    public ResponseObject<Boolean> deleteSkill(final Long skillId) {
        try {
            Optional<SkillEntity> optionalSkillEntity = skillRepository.findSkillEntityBySkillId(skillId);

            if(optionalSkillEntity.isEmpty())
                return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No skill having such id", null);

            skillRepository.delete(optionalSkillEntity.get());
            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error deleting skill having such skill id", null);
        }
    }
}
