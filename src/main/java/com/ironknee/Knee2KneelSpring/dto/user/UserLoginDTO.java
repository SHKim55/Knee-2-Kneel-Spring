package com.ironknee.Knee2KneelSpring.dto.user;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    private String password;
    private String email;
}
