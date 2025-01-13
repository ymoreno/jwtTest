package org.jwtTest.controller;

import lombok.extern.slf4j.Slf4j;
import org.jwtTest.exception.ErrorResponse;
import org.jwtTest.model.User;
import org.jwtTest.model.UserRequest;
import org.jwtTest.model.UserResponse;
import org.jwtTest.service.UserService;
import org.jwtTest.util.JwtUtil;
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
    public ResponseEntity<?> login(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");

        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(401, "Invalid token"));
        }

        try {
            User user = userService.getUser(tokenHeader);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(401, "User not found or inactive"));
            }
        } catch (Exception e) {
            log.error("Error getting user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }

    }
}
