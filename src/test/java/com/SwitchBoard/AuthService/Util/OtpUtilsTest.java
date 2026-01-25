package com.SwitchBoard.AuthService.Util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OTP Utils Test")
class OtpUtilsTest {

    @Test
    @DisplayName("Should generate a 6-digit OTP")
    void testGenerateOtp() {
        // Act
        String otp = OtpUtils.generateOtp();

        // Assert
        assertNotNull(otp, "Generated OTP should not be null");
        assertEquals(6, otp.length(), "OTP should be 6 digits long");
        assertTrue(otp.matches("\\d{6}"), "OTP should contain only digits");
        
        int otpInt = Integer.parseInt(otp);
        assertTrue(otpInt >= 100000 && otpInt <= 999999, "OTP should be between 100000 and 999999");
    }

    @Test
    @DisplayName("Should generate different OTPs on multiple calls")
    void testGenerateOtpUniqueness() {
        // Act
        String otp1 = OtpUtils.generateOtp();
        String otp2 = OtpUtils.generateOtp();
        String otp3 = OtpUtils.generateOtp();

        // Assert - at least one should be different (extremely high probability)
        boolean allDifferent = !otp1.equals(otp2) || !otp2.equals(otp3) || !otp1.equals(otp3);
        assertTrue(allDifferent, "Multiple OTP generations should produce different values");
    }

    @Test
    @DisplayName("Should hash OTP using SHA-256")
    void testHashOtp() {
        // Arrange
        String otp = "123456";

        // Act
        String hashedOtp = OtpUtils.hashOtp(otp);

        // Assert
        assertNotNull(hashedOtp, "Hashed OTP should not be null");
        assertEquals(64, hashedOtp.length(), "SHA-256 hash should be 64 characters long");
        assertTrue(hashedOtp.matches("[a-f0-9]{64}"), "Hash should be hexadecimal");
    }

    @Test
    @DisplayName("Should produce consistent hash for same OTP")
    void testHashOtpConsistency() {
        // Arrange
        String otp = "654321";

        // Act
        String hash1 = OtpUtils.hashOtp(otp);
        String hash2 = OtpUtils.hashOtp(otp);

        // Assert
        assertEquals(hash1, hash2, "Same OTP should produce same hash");
    }

    @Test
    @DisplayName("Should produce different hashes for different OTPs")
    void testHashOtpUniqueness() {
        // Arrange
        String otp1 = "123456";
        String otp2 = "654321";

        // Act
        String hash1 = OtpUtils.hashOtp(otp1);
        String hash2 = OtpUtils.hashOtp(otp2);

        // Assert
        assertNotEquals(hash1, hash2, "Different OTPs should produce different hashes");
    }

    @Test
    @DisplayName("Should handle edge case OTPs")
    void testHashOtpEdgeCases() {
        // Test minimum valid OTP
        String minOtp = "100000";
        String minHash = OtpUtils.hashOtp(minOtp);
        assertNotNull(minHash);
        assertEquals(64, minHash.length());

        // Test maximum valid OTP
        String maxOtp = "999999";
        String maxHash = OtpUtils.hashOtp(maxOtp);
        assertNotNull(maxHash);
        assertEquals(64, maxHash.length());

        // Hashes should be different
        assertNotEquals(minHash, maxHash);
    }
}
