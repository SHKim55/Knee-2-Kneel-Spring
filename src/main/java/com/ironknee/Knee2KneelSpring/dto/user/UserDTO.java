package com.ironknee.Knee2KneelSpring.dto.user;

import com.ironknee.Knee2KneelSpring.service.user.MatchStatus;
import com.ironknee.Knee2KneelSpring.service.user.UserRank;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID userId;
    private String nickname;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    private Long exp;
    private Long level;
    private Long rankPoint;

    @Enumerated(EnumType.STRING)
    private UserRank userRank;
}
