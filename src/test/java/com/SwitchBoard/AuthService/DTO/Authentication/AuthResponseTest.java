package com.SwitchBoard.AuthService.DTO.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth Response DTO Test")
class AuthResponseTest {

    @Test
    @DisplayName("Should create auth response with builder")
    void testBuilder() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token-123")
                .refreshToken("refresh-token-456")
                .expiresIn(3600L)
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("access-token-123", response.getAccessToken());
        assertEquals("refresh-token-456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    @DisplayName("Should have default token type as Bearer")
    void testDefaultTokenType() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken("token")
                .build();

        // Assert
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    @DisplayName("Should support custom token type")
    void testCustomTokenType() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken("token")
                .tokenType("Custom")
                .build();

        // Assert
        assertEquals("Custom", response.getTokenType());
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Act
        AuthResponse response = new AuthResponse(
                "access", "refresh", "Bearer", 7200L
        );

        // Assert
        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(7200L, response.getExpiresIn());
    }

    @Test
    @DisplayName("Should support no-args constructor and setters")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        AuthResponse response = new AuthResponse();

        // Act
        response.setAccessToken("new-access");
        response.setRefreshToken("new-refresh");
        response.setTokenType("JWT");
        response.setExpiresIn(1800L);

        // Assert
        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
        assertEquals("JWT", response.getTokenType());
        assertEquals(1800L, response.getExpiresIn());
    }

    @Test
    @DisplayName("Should handle long token strings")
    void testLongTokens() {
        // Arrange
        String longAccessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.signature";
        String longRefreshToken = "550e8400-e29b-41d4-a716-446655440000-550e8400-e29b-41d4-a716-446655440000";

        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken(longAccessToken)
                .refreshToken(longRefreshToken)
                .expiresIn(3600L)
                .build();

        // Assert
        assertEquals(longAccessToken, response.getAccessToken());
        assertEquals(longRefreshToken, response.getRefreshToken());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        AuthResponse response = AuthResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .expiresIn(null)
                .build();

        // Assert
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getExpiresIn());
        assertEquals("Bearer", response.getTokenType()); // Default value
    }

    @Test
    @DisplayName("Should handle different expiration times")
    void testDifferentExpirationTimes() {
        // Act
        AuthResponse response1 = AuthResponse.builder().expiresIn(300L).build(); // 5 minutes
        AuthResponse response2 = AuthResponse.builder().expiresIn(3600L).build(); // 1 hour
        AuthResponse response3 = AuthResponse.builder().expiresIn(86400L).build(); // 1 day

        // Assert
        assertEquals(300L, response1.getExpiresIn());
        assertEquals(3600L, response2.getExpiresIn());
        assertEquals(86400L, response3.getExpiresIn());
    }
}
