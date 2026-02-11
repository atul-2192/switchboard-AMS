package com.SwitchBoard.AuthService.DTO.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth Request DTO Test")
class AuthRequestTest {

    @Test
    @DisplayName("Should create auth request with email")
    void testAuthRequest() {
        // Arrange
        AuthRequest request = new AuthRequest();

        // Act
        request.setEmail("test@example.com");

        // Assert
        assertNotNull(request);
        assertEquals("test@example.com", request.getEmail());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testDifferentEmailFormats() {
        // Arrange & Act
        AuthRequest request1 = new AuthRequest();
        request1.setEmail("simple@example.com");

        AuthRequest request2 = new AuthRequest();
        request2.setEmail("user+tag@example.co.uk");

        AuthRequest request3 = new AuthRequest();
        request3.setEmail("user.name@sub.domain.com");

        // Assert
        assertEquals("simple@example.com", request1.getEmail());
        assertEquals("user+tag@example.co.uk", request2.getEmail());
        assertEquals("user.name@sub.domain.com", request3.getEmail());
    }

    @Test
    @DisplayName("Should handle null email")
    void testNullEmail() {
        // Arrange
        AuthRequest request = new AuthRequest();

        // Act
        request.setEmail(null);

        // Assert
        assertNull(request.getEmail());
    }

    @Test
    @DisplayName("Should handle empty email")
    void testEmptyEmail() {
        // Arrange
        AuthRequest request = new AuthRequest();

        // Act
        request.setEmail("");

        // Assert
        assertEquals("", request.getEmail());
    }

    @Test
    @DisplayName("Should handle email with spaces")
    void testEmailWithSpaces() {
        // Arrange
        AuthRequest request = new AuthRequest();
        String emailWithSpaces = " test@example.com ";

        // Act
        request.setEmail(emailWithSpaces);

        // Assert
        assertEquals(emailWithSpaces, request.getEmail());
    }
}
