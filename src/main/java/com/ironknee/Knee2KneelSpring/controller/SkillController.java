package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.player.SkillCreateDTO;
import com.ironknee.Knee2KneelSpring.dto.player.SkillDTO;
import com.ironknee.Knee2KneelSpring.service.player.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skill")
public class SkillController {
    private final SkillService skillService;

    public SkillController(final SkillService skillService) {
        this.skillService = skillService;
    }

    // 스킬 생성
    // 새로운 스킬 생성 후 DB에 저장 (개발용 API)
    @PostMapping("/create")
    public ResponseObject<SkillDTO> createSkill(@RequestBody SkillCreateDTO skillCreateDTO) {
        return skillService.createSkill(skillCreateDTO);
    }

    // 스킬 검색
    // 현재 DB에 저장된 스킬 목록 반환 (DISTINCT)
    @GetMapping("/search")
    public ResponseObject<List<SkillDTO>> searchSkills() {
        return skillService.searchSkills();
    }

    // 유저 스킬 검색
    // 현재 유저가 보유한 스킬 목록 반환
    @GetMapping("/search/user")
    public ResponseObject<List<SkillDTO>> searchUserSkills(@RequestHeader("Authorization") String token) {
        return skillService.searchUserSkills(token);
    }

    // 스킬 부여
    // 새로운 스킬을 특정 유저에게 부여함 (unlock)
    @PostMapping("/grant/{skillNum}")
    public ResponseObject<Boolean> grantSkill(@RequestHeader("Authorization") String token, @PathVariable Long skillNum) {
        return skillService.grantSkill(token, skillNum);
    }

    // 스킬 부여 해제
    // 특정 유저에게 부여한 스킬을 접근 해제함
    @PostMapping("/revoke/{skillNum}")
    public ResponseObject<Boolean> revokeSkill(@RequestHeader("Authorization") String token, @PathVariable Long skillNum) {
        return skillService.revokeSkill(token, skillNum);
    }

    // 스킬 삭제
    // 해당 스킬을 DB에서 삭제함 (개발용 API)
    @DeleteMapping("/delete/{skillId}")
    public ResponseObject<Boolean> deleteSkill(@PathVariable Long skillId) {
        return skillService.deleteSkill(skillId);
    }
}