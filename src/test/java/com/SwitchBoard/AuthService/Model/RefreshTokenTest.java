package com.SwitchBoard.AuthService.Model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RefreshToken Model Test")
class RefreshTokenTest {

    @Test
    @DisplayName("Should create refresh token with builder")
    void testRefreshTokenBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        String token = "refresh-token-value";
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .build();

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .id(id)
                .token(token)
                .expiryDate(expiryDate)
                .account(account)
                .isRevoked(false)
                .build();

        // Assert
        assertNotNull(refreshToken);
        assertEquals(id, refreshToken.getId());
        assertEquals(token, refreshToken.getToken());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertEquals(account, refreshToken.getAccount());
        assertFalse(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should default isRevoked to false")
    void testDefaultIsRevoked() {
        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        // Assert
        assertFalse(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should support setters and getters")
    void testRefreshTokenSettersGetters() {
        // Arrange
        RefreshToken refreshToken = new RefreshToken();
        UUID id = UUID.randomUUID();
        String token = "new-token";
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        LocalDateTime createdAt = LocalDateTime.now();
        Account account = Account.builder()
                .email("test@example.com")
                .build();

        // Act
        refreshToken.setId(id);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setCreatedAt(createdAt);
        refreshToken.setAccount(account);
        refreshToken.setIsRevoked(true);

        // Assert
        assertEquals(id, refreshToken.getId());
        assertEquals(token, refreshToken.getToken());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertEquals(createdAt, refreshToken.getCreatedAt());
        assertEquals(account, refreshToken.getAccount());
        assertTrue(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should create revoked token")
    void testRevokedToken() {
        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("revoked-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(true)
                .build();

        // Assert
        assertTrue(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should create expired token")
    void testExpiredToken() {
        // Arrange
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("expired-token")
                .expiryDate(pastDate)
                .isRevoked(false)
                .build();

        // Assert
        assertTrue(refreshToken.getExpiryDate().isBefore(LocalDateTime.now()));
        assertFalse(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should handle long token values")
    void testLongTokenValue() {
        // Arrange
        String longToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token(longToken)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        // Assert
        assertEquals(longToken, refreshToken.getToken());
        assertTrue(refreshToken.getToken().length() > 50);
    }

    @Test
    @DisplayName("Should associate token with account")
    void testTokenAccountAssociation() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder()
                .id(accountId)
                .email("user@example.com")
                .name("User Name")
                .build();

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .account(account)
                .build();

        // Assert
        assertNotNull(refreshToken.getAccount());
        assertEquals(accountId, refreshToken.getAccount().getId());
        assertEquals("user@example.com", refreshToken.getAccount().getEmail());
        assertEquals("User Name", refreshToken.getAccount().getName());
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        String token = "test-token";
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        LocalDateTime createdAt = LocalDateTime.now();
        Account account = Account.builder().email("test@example.com").build();

        // Act
        RefreshToken refreshToken = new RefreshToken(id, token, expiryDate, account, createdAt, false);

        // Assert
        assertNotNull(refreshToken);
        assertEquals(id, refreshToken.getId());
        assertEquals(token, refreshToken.getToken());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertEquals(account, refreshToken.getAccount());
        assertEquals(createdAt, refreshToken.getCreatedAt());
        assertFalse(refreshToken.getIsRevoked());
    }

    @Test
    @DisplayName("Should support no-args constructor")
    void testNoArgsConstructor() {
        // Act
        RefreshToken refreshToken = new RefreshToken();

        // Assert
        assertNotNull(refreshToken);
    }

    @Test
    @DisplayName("Should handle future expiry dates")
    void testFutureExpiryDate() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusMonths(6);

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("long-lived-token")
                .expiryDate(futureDate)
                .build();

        // Assert
        assertTrue(refreshToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertTrue(refreshToken.getExpiryDate().isAfter(LocalDateTime.now().plusDays(30)));
    }

    @Test
    @DisplayName("Should track creation timestamp")
    void testCreationTimestamp() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        LocalDateTime createdAt = LocalDateTime.now();
        
        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token("test-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .createdAt(createdAt)
                .build();

        // Assert
        assertNotNull(refreshToken.getCreatedAt());
        assertTrue(refreshToken.getCreatedAt().isAfter(beforeCreation));
    }
}
