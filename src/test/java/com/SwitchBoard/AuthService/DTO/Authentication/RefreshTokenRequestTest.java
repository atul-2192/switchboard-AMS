package com.SwitchBoard.AuthService.DTO.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Refresh Token Request DTO Test")
class RefreshTokenRequestTest {

    @Test
    @DisplayName("Should create refresh token request with all-args constructor")
    void testAllArgsConstructor() {
        // Act
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-123");

        // Assert
        assertNotNull(request);
        assertEquals("refresh-token-123", request.getRefreshToken());
    }

    @Test
    @DisplayName("Should create refresh token request with no-args constructor")
    void testNoArgsConstructor() {
        // Act
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("new-token");

        // Assert
        assertEquals("new-token", request.getRefreshToken());
    }

    @Test
    @DisplayName("Should handle long token strings")
    void testLongTokenString() {
        // Arrange
        String longToken = "550e8400-e29b-41d4-a716-446655440000-550e8400-e29b-41d4-a716-446655440000-550e8400-e29b-41d4-a716-446655440000";

        // Act
        RefreshTokenRequest request = new RefreshTokenRequest(longToken);

        // Assert
        assertEquals(longToken, request.getRefreshToken());
        assertTrue(request.getRefreshToken().length() > 100);
    }

    @Test
    @DisplayName("Should handle null token")
    void testNullToken() {
        // Act
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        // Assert
        assertNull(request.getRefreshToken());
    }

    @Test
    @DisplayName("Should handle empty token")
    void testEmptyToken() {
        // Act
        RefreshTokenRequest request = new RefreshTokenRequest("");

        // Assert
        assertEquals("", request.getRefreshToken());
    }

    @Test
    @DisplayName("Should support setter")
    void testSetter() {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();

        // Act
        request.setRefreshToken("updated-token");

        // Assert
        assertEquals("updated-token", request.getRefreshToken());
    }

    @Test
    @DisplayName("Should handle UUID format tokens")
    void testUuidFormatToken() {
        // Arrange
        String uuidToken = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        RefreshTokenRequest request = new RefreshTokenRequest(uuidToken);

        // Assert
        assertEquals(uuidToken, request.getRefreshToken());
        assertTrue(request.getRefreshToken().contains("-"));
    }
}
