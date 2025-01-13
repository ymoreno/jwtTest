package org.jwttest.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET_KEY = "EstaEsUnaClaveSecretaMuySeguraYLarga123!";
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateToken_shouldGenerateTokenWithSubject() {
        String subject = "test@example.com";

        String token = jwtUtil.generateToken(subject);

        assertNotNull(token, "Token should not be null");
        assertTrue(token.length() > 0, "Token should not be empty");
    }

    @Test
    void extractSubject_shouldReturnSubjectFromValidToken() {
        String subject = "test@example.com";
        String token = jwtUtil.generateToken(subject);

        String extractedSubject = jwtUtil.extractSubject(token);

        assertEquals(subject, extractedSubject, "Extracted subject should match the original subject");
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String subject = "test@example.com";
        String token = jwtUtil.generateToken(subject);

        boolean isValid = jwtUtil.isTokenValid(token);

        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        String invalidToken = "InvalidTokenExample";

        boolean isValid = jwtUtil.isTokenValid(invalidToken);

        assertFalse(isValid, "Invalid token should not be valid");
    }

    @Test
    void isTokenValid_shouldReturnFalseForTamperedToken() {
        String subject = "test@example.com";
        String token = jwtUtil.generateToken(subject);
        String tamperedToken = token + "tampered";

        boolean isValid = jwtUtil.isTokenValid(tamperedToken);

        assertFalse(isValid, "Tampered token should not be valid");
    }

    @Test
    void getClaims_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "InvalidTokenExample";

        assertThrows(Exception.class, () -> {
            jwtUtil.extractSubject(invalidToken);
        }, "Extracting claims from an invalid token should throw an exception");
    }
}
