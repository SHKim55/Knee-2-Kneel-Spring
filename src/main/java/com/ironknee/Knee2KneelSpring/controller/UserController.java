package com.ironknee.Knee2KneelSpring.controller;

import com.ironknee.Knee2KneelSpring.dto.ResponseCode;
import com.ironknee.Knee2KneelSpring.dto.ResponseObject;
import com.ironknee.Knee2KneelSpring.dto.user.UserDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserLoginDTO;
import com.ironknee.Knee2KneelSpring.dto.user.UserRegisterDTO;
import com.ironknee.Knee2KneelSpring.service.game.GameService;
import com.ironknee.Knee2KneelSpring.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final GameService gameService;

    public UserController(final UserService userService, final GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
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
    public ResponseObject<UserDTO> getUserInfo(@RequestHeader(name = "Authorization") String token) {
        Map<String, Object> userMap = userService.getUserInfo(token);

        if(userMap.containsKey("error")) {
            return new ResponseObject<>(ResponseCode.fail.toString(), userMap.get("error").toString(), null);
        } else if(userMap.containsKey("new")) {  // 신규 로그인
            return new ResponseObject<>(ResponseCode.success.toString(), "success", (UserDTO) userMap.get("new"));
        }

        // 재접속
        gameService.initializeMatch((UserDTO) userMap.get("reconnect"));
        return new ResponseObject<>(ResponseCode.success.toString(), "success", (UserDTO) userMap.get("reconnect"));
    }

    @DeleteMapping("/signOut")
    public ResponseObject<Boolean> signOut(@RequestHeader(name = "Authorization") String token) { return userService.signOut(token); }
}
