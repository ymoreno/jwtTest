package org.jwtTest.service.impl;

import org.jwtTest.model.Phone;
import org.jwtTest.model.User;
import org.jwtTest.model.UserRequest;
import org.jwtTest.model.UserResponse;
import org.jwtTest.persistence.UserRepository;
import org.jwtTest.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        UserRequest userRequest = UserRequest.builder()
                .email("test@example.com")
                .name("Test User")
                .password("a2asfGfdfdf4")
                .phones(Collections.singletonList(UserRequest.Phone.builder()
                        .number(123456789L)
                        .citycode(1)
                        .contrycode("44")
                        .build()))
                .build();

        when(jwtUtil.generateToken(userRequest.getEmail())).thenReturn("test-token");
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse response = userService.createUser(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals("test-token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_InvalidEmail() {
        // Arrange
        UserRequest userRequest = UserRequest.builder()
                .email("invalid-email")
                .name("Test User")
                .password("ValidPassword123!")
                .phones(Collections.emptyList())
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUser_Success() {
        // Arrange
        String token = "Bearer test-token";
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .id(UUID.randomUUID())
                .token("old-token")
                .lastLogin(new Date())
                .build();

        when(jwtUtil.extractSubject("test-token")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(email)).thenReturn("new-token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.getUser(token);

        // Assert
        assertNotNull(result);
        assertEquals("new-token", result.getToken());
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testGetUser_UserNotFound() {
        // Arrange
        String token = "Bearer test-token";
        String email = "nonexistent@example.com";

        when(jwtUtil.extractSubject("test-token")).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUser(token);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateToken_Success() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .id(UUID.randomUUID())
                .token("old-token")
                .lastLogin(new Date())
                .build();

        when(jwtUtil.generateToken(user.getEmail())).thenReturn("new-token");
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User result = userService.updateToken(user);

        // Assert
        assertNotNull(result);
        assertEquals("new-token", result.getToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindUserByMail_Success() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder()
                .email(email)
                .id(UUID.randomUUID())
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findUserByMail(email);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testFindUserByMail_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findUserByMail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
