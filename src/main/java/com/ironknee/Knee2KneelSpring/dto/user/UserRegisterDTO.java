package com.ironknee.Knee2KneelSpring.dto.user;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    private String nickname;
    private String password;
    private String email;
}
