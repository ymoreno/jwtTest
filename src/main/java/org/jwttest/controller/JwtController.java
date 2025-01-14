package org.jwttest.controller;

import lombok.extern.slf4j.Slf4j;
import org.jwttest.exception.ErrorResponse;
import org.jwttest.model.User;
import org.jwttest.model.UserRequest;
import org.jwttest.model.UserResponse;
import org.jwttest.service.UserService;
import org.jwttest.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class JwtController {

    private final JwtUtil jwtUtil;

    private UserService userService;

    public JwtController(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserRequest userRequest) {
        UserResponse user = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestHeader("Authorization") String tokenHeader) {
        User user = userService.getUserByToken(tokenHeader);
        return ResponseEntity.ok(user);

    }
}
