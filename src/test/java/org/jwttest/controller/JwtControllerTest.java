package org.jwttest.controller;

import org.jwttest.exception.ErrorResponse;
import org.jwttest.model.User;
import org.jwttest.model.UserRequest;
import org.jwttest.model.UserResponse;
import org.jwttest.service.UserService;
import org.jwttest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    private JwtController jwtController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtController = new JwtController(jwtUtil, userService);
    }

    @Test
    void signUp_shouldReturnCreatedStatusWithUserResponse() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("Password123");
        userRequest.setName("Test User");

        UUID id = UUID.randomUUID();

        UserResponse mockUserResponse = UserResponse.builder()
                .id(id)
                .token("mockToken")
                .created(null)
                .isActive(true)
                .lastLogin(null)
                .build();

        when(userService.createUser(userRequest)).thenReturn(mockUserResponse);

        ResponseEntity<UserResponse> response = jwtController.signUp(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertEquals("mockToken", response.getBody().getToken());
        verify(userService, times(1)).createUser(userRequest);
    }

    @Test
    void login_shouldReturnOkStatusWithUser_whenTokenIsValid() {
        String tokenHeader = "Bearer validToken";
        String token = "validToken";
        UUID id = UUID.randomUUID();

        User mockUser = User.builder()
                .id(id)
                .email("test@example.com")
                .isActive(true)
                .build();

        when(userService.getUserByToken(tokenHeader)).thenReturn(mockUser);

        ResponseEntity<User> response = jwtController.login(tokenHeader);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof User);
        assertEquals(id, ((User) response.getBody()).getId());
        verify(userService, times(1)).getUserByToken(tokenHeader);
    }

}
