package com.ironknee.Knee2KneelSpring.service.player;

import lombok.*;

import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private UUID userId;
    private String userEmail;
    private PlayerRole playerRole;
    private Boolean isAdmin;
    private Boolean isReady;

//    private List<Skill> skillList;
//    private PlayerConcept playerConcept;

    public static PlayerRole getRandomRole() {
        PlayerRole[] playerRoles = PlayerRole.values();

        Random random = new Random();
        int randomIndex = random.nextInt(playerRoles.length - 1) + 1;   // 교수(index 0)는 랜덤 역할 생성에서 제외

        return playerRoles[randomIndex];
    }
}
