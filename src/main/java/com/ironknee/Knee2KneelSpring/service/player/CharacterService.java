package com.ironknee.Knee2KneelSpring.service.player;

import com.ironknee.Knee2KneelSpring.authentication.JwtUtil;
import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.player.CharacterCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.player.CharacterDTO;
import com.ironknee.Knee2KneelSpring.entity.CharacterEntity;
import com.ironknee.Knee2KneelSpring.entity.UserEntity;
import com.ironknee.Knee2KneelSpring.repository.CharacterRepository;
import com.ironknee.Knee2KneelSpring.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {
    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public CharacterService(final CharacterRepository characterRepository, final UserRepository userRepository,
                            final JwtUtil jwtUtil) {
        this.characterRepository = characterRepository;
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
    public static CharacterDTO convertEntityToDTO(final CharacterEntity characterEntity) {
        return CharacterDTO.builder()
                .characterId(characterEntity.getCharacterId())
                .characterNum(characterEntity.getCharacterNum())
                .characterName(characterEntity.getCharacterName())
                .build();
    }

    @Transactional
    public ResponseObject<CharacterDTO> createCharacter(final CharacterCreateDTO characterCreateDTO) {
        try {
            CharacterEntity characterEntity = CharacterEntity.builder()
                    .characterNum(characterCreateDTO.getCharacterNum())
                    .characterName(characterCreateDTO.getCharacterName())
                    .build();

            characterEntity = characterRepository.save(characterEntity);
            return new ResponseObject<>(ResponseCode.success.toString(), "success", convertEntityToDTO(characterEntity));
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred in saving character info", null);
        }
    }

    public ResponseObject<List<CharacterDTO>> searchCharacters() {
        try {
            List<CharacterEntity> characterEntityList = characterRepository.findAll();

            List<CharacterDTO> characterDTOList = new ArrayList<>();
            characterEntityList.forEach(
                    character -> characterDTOList.add(convertEntityToDTO(character))
            );

            return new ResponseObject<>(ResponseCode.success.toString(), "success", characterDTOList);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while loading character list", null);
        }
    }

    public ResponseObject<List<CharacterDTO>> searchUserCharacters(String token) {
        try {
            UserEntity userEntity = findUserByToken(token);

            List<CharacterDTO> characterDTOList = new ArrayList<>();
            userEntity.getCharacters().forEach(
                    characterEntity -> characterDTOList.add(convertEntityToDTO(characterEntity))
            );

            return new ResponseObject<>(ResponseCode.success.toString(), "success", characterDTOList);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while loading character list for user", null);
        }
    }

    @Transactional
    public ResponseObject<Boolean> grantCharacter(final String token, final Long characterNum) {
        if(!characterRepository.existsByCharacterNum(characterNum))
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No character having such character number", null);

        UserEntity userEntity = findUserByToken(token);
        CharacterEntity characterEntity = characterRepository.findCharacterEntityByCharacterNum(characterNum).orElse(null);

        try {
            List<UserEntity> characterUsers = characterEntity.getUsers();
            List<CharacterEntity> userCharacters = userEntity.getCharacters();
            characterUsers.add(userEntity);
            characterEntity.setUsers(characterUsers);
            userCharacters.add(characterEntity);
            userEntity.setCharacters(userCharacters);

            characterRepository.save(characterEntity);
            userRepository.save(userEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while adding character to user", null);
        }
    }

    @Transactional
    public ResponseObject<Boolean> revokeCharacter(final String token, final Long characterNum) {
        if(!characterRepository.existsByCharacterNum(characterNum))
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No character having such character number", null);

        UserEntity userEntity = findUserByToken(token);
        CharacterEntity characterEntity = characterRepository.findCharacterEntityByCharacterNum(characterNum).orElse(null);

        try {
            List<UserEntity> characterUsers = characterEntity.getUsers();
            List<CharacterEntity> userCharacters = userEntity.getCharacters();
            characterUsers.remove(userEntity);
            characterEntity.setUsers(characterUsers);
            userCharacters.remove(characterEntity);
            userEntity.setCharacters(userCharacters);

            characterRepository.save(characterEntity);
            userRepository.save(userEntity);

            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "Server Error : Error occurred while removing character to user", null);
        }
    }

    @Transactional
    public ResponseObject<Boolean> deleteCharacter(final Long characterId) {
        try {
            Optional<CharacterEntity> optionalCharacterEntity = characterRepository.findCharacterEntityByCharacterId(characterId);

            if(optionalCharacterEntity.isEmpty())
                return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : No character having such id", null);

            characterRepository.delete(optionalCharacterEntity.get());
            return new ResponseObject<>(ResponseCode.success.toString(), "success", true);
        } catch (Exception e) {
            return new ResponseObject<>(ResponseCode.fail.toString(), "DB Error : Error deleting character having such character id", null);
        }
    }
}
