package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserLoginDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserRegisterDTO;
import com.ironknee.Knee2KneelSpring.security.JwtUtil;
import com.ironknee.Knee2KneelSpring.service.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(final UserService userService, final AuthenticationManager authenticationManager,
                          final JwtUtil jwtUtil, final PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseObject<UserDTO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/logIn")
    public ResponseObject<String> logIn(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.logIn(userLoginDTO);
    }

    @GetMapping("/info")
    public ResponseObject<UserDTO> getUserInfo(@RequestBody UUID userId) {
        return userService.getUserInfo(userId);
    }

    @DeleteMapping("/signOut")
    public ResponseObject<Boolean> signOut(@RequestBody UUID userId) { return userService.signOut(userId); }
}
