package com.ironknee.Knee2KneelSpring.service.player;

import com.ironknee.Knee2KneelSpring.service.game.Game;
import com.ironknee.Knee2KneelSpring.service.user.UserRank;
import lombok.*;

import java.util.List;
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
    private String userNickname;
    private Long level;
    private UserRank userRank;
    private Long rankPoint;
    private PlayerRole playerRole;
    private Boolean isAdmin;
    private Boolean isReady;

//    private List<Skill> skillList;
//    private PlayerCharacter playerCharacter;

    public static PlayerRole getRandomRole() {
        PlayerRole[] playerRoles = PlayerRole.values();

        Random random = new Random();
        int randomIndex = random.nextInt(playerRoles.length - 1) + 1;   // 교수(index 0)는 랜덤 역할 생성에서 제외

        return playerRoles[randomIndex];
    }

    public static PlayerRole grantPlayerRole(final Game game) {
        boolean hasProfessor = false;
        long countStudent = 0L, maxStudent = 5L;

        List<Player> playerList = game.getPlayerList();

        for(Player player : playerList) {
            if(player.getPlayerRole() == PlayerRole.professor)
                hasProfessor = true;
            else if(player.getPlayerRole() == PlayerRole.student)
                countStudent++;
        }

        if(!hasProfessor) return PlayerRole.professor;
        if(countStudent <= maxStudent - 1) return PlayerRole.student;
        else return PlayerRole.assistant;
    }
}
