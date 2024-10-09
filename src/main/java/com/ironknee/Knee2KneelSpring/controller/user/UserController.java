package com.ironknee.Knee2KneelSpring.controller.user;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserLoginDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserRegisterDTO;
import com.ironknee.Knee2KneelSpring.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseObject<UserDTO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/logIn")
    public ResponseObject<UserDTO> logIn(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.logIn(userLoginDTO);
    }

    @GetMapping("/info")
    public ResponseObject<UserDTO> getUserInfo(@RequestBody UUID userId) {
        return userService.getUserInfo(userId);
    }

    @DeleteMapping("/signOut")
    public ResponseObject<Boolean> signOut(@RequestBody UUID userId) { return userService.signOut(userId); }
}
