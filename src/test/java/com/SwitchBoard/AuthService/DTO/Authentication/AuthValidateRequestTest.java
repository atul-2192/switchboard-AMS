package com.SwitchBoard.AuthService.DTO.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Auth Validate Request DTO Test")
class AuthValidateRequestTest {

    @Test
    @DisplayName("Should create validate request with email and OTP")
    void testAuthValidateRequest() {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();

        // Act
        request.setEmail("test@example.com");
        request.setOtp("123456");

        // Assert
        assertNotNull(request);
        assertEquals("test@example.com", request.getEmail());
        assertEquals("123456", request.getOtp());
    }

    @Test
    @DisplayName("Should handle 6-digit OTP")
    void testSixDigitOtp() {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();

        // Act
        request.setOtp("654321");

        // Assert
        assertEquals("654321", request.getOtp());
        assertEquals(6, request.getOtp().length());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testDifferentEmails() {
        // Arrange & Act
        AuthValidateRequest request = new AuthValidateRequest();
        request.setEmail("user@domain.com");
        request.setOtp("111111");

        // Assert
        assertEquals("user@domain.com", request.getEmail());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();

        // Act
        request.setEmail(null);
        request.setOtp(null);

        // Assert
        assertNull(request.getEmail());
        assertNull(request.getOtp());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();

        // Act
        request.setEmail("");
        request.setOtp("");

        // Assert
        assertEquals("", request.getEmail());
        assertEquals("", request.getOtp());
    }
}
