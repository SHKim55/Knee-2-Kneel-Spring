package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.player.CharacterCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.player.CharacterDTO;
import com.ironknee.Knee2KneelSpring.service.player.CharacterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/character")
public class CharacterController {
    private final CharacterService characterService;

    public CharacterController(final CharacterService characterService) {
        this.characterService = characterService;
    }

    // 캐릭터 생성
    // 새로운 캐릭터 생성 후 DB에 저장 (개발용 API)
    @PostMapping("/create")
    public ResponseObject<CharacterDTO> createCharacter(@RequestBody CharacterCreateDTO characterCreateDTO) {
        return characterService.createCharacter(characterCreateDTO);
    }

    // 캐릭터 검색
    // 현재 DB에 저장된 캐릭터 목록 반환 (DISTINCT)
    @GetMapping("/search")
    public ResponseObject<List<CharacterDTO>> searchCharacters() {
        return characterService.searchCharacters();
    }

    // 유저 캐릭터 검색
    // 특정 유저에게 부여된 캐릭터 목록을 반환함
    @GetMapping("/search/user")
    public ResponseObject<List<CharacterDTO>> searchUserCharacters(@RequestHeader("Authorization") String token) {
        return characterService.searchUserCharacters(token);
    }

    // 캐릭터 부여
    // 새로운 캐릭터를 특정 유저에게 부여함 (unlock)
    @PostMapping("/grant/{characterNum}")
    public ResponseObject<Boolean> grantCharacter(@RequestHeader("Authorization") String token, @PathVariable Long characterNum) {
        return characterService.grantCharacter(token, characterNum);
    }

    // 캐릭터 부여 해제
    // 특정 유저에게 부여한 캐릭터를 접근 해제함
    @PostMapping("/revoke/{characterNum}")
    public ResponseObject<Boolean> revokeCharacter(@RequestHeader("Authorization") String token, @PathVariable Long characterNum) {
        return characterService.revokeCharacter(token, characterNum);
    }

    // 캐릭터 삭제
    // 해당 캐릭터를 DB에서 삭제함 (개발용 API)
    @DeleteMapping("/delete/{characterId}")
    public ResponseObject<Boolean> deleteCharacter(@PathVariable Long characterId) {
        return characterService.deleteCharacter(characterId);
    }
}
