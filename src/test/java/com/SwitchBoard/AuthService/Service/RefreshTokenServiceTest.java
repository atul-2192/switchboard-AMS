package com.SwitchBoard.AuthService.Service;

import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Model.RefreshToken;
import com.SwitchBoard.AuthService.Repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Refresh Token Service Test")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private Account testAccount;
    private static final Long REFRESH_TOKEN_EXPIRATION = 604800L; // 7 days in seconds

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);
        
        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .build();
    }

    @Test
    @DisplayName("Should create refresh token successfully")
    void testCreateRefreshToken() {
        // Arrange
        RefreshToken expectedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("test-token-uuid")
                .account(testAccount)
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION))
                .isRevoked(false)
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(testAccount);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount, result.getAccount());
        assertFalse(result.getIsRevoked());
        
        // Verify that existing tokens were revoked
        verify(refreshTokenRepository).revokeAllTokensByAccount(testAccount);
        verify(refreshTokenRepository).save(any(RefreshToken.class));

        // Verify the saved token properties
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();
        
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getToken().contains("-")); // UUID format check
        assertEquals(testAccount, savedToken.getAccount());
        assertNotNull(savedToken.getExpiryDate());
    }

    @Test
    @DisplayName("Should revoke all tokens before creating new one")
    void testCreateRefreshTokenRevokesExisting() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

        // Act
        refreshTokenService.createRefreshToken(testAccount);

        // Assert
        verify(refreshTokenRepository).revokeAllTokensByAccount(testAccount);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Should find valid token by token string")
    void testFindByToken() {
        // Arrange
        String tokenString = "valid-token-uuid";
        RefreshToken expectedToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(tokenString)
                .account(testAccount)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .isRevoked(false)
                .build();

        when(refreshTokenRepository.findValidToken(eq(tokenString), any(LocalDateTime.class)))
                .thenReturn(Optional.of(expectedToken));

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(tokenString, result.get().getToken());
        assertEquals(testAccount, result.get().getAccount());
        verify(refreshTokenRepository).findValidToken(eq(tokenString), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void testFindByTokenNotFound() {
        // Arrange
        String tokenString = "non-existent-token";
        when(refreshTokenRepository.findValidToken(eq(tokenString), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        // Assert
        assertFalse(result.isPresent());
        verify(refreshTokenRepository).findValidToken(eq(tokenString), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should validate token as valid when not revoked and not expired")
    void testIsTokenValid() {
        // Arrange
        RefreshToken validToken = RefreshToken.builder()
                .token("valid-token")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .isRevoked(false)
                .build();

        // Act
        boolean result = refreshTokenService.isTokenValid(validToken);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate token as invalid when revoked")
    void testIsTokenInvalidWhenRevoked() {
        // Arrange
        RefreshToken revokedToken = RefreshToken.builder()
                .token("revoked-token")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .isRevoked(true)
                .build();

        // Act
        boolean result = refreshTokenService.isTokenValid(revokedToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate token as invalid when expired")
    void testIsTokenInvalidWhenExpired() {
        // Arrange
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expired-token")
                .expiryDate(LocalDateTime.now().minusDays(1))
                .isRevoked(false)
                .build();

        // Act
        boolean result = refreshTokenService.isTokenValid(expiredToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate token as invalid when both revoked and expired")
    void testIsTokenInvalidWhenRevokedAndExpired() {
        // Arrange
        RefreshToken invalidToken = RefreshToken.builder()
                .token("invalid-token")
                .expiryDate(LocalDateTime.now().minusDays(1))
                .isRevoked(true)
                .build();

        // Act
        boolean result = refreshTokenService.isTokenValid(invalidToken);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Should revoke all tokens by account")
    void testRevokeAllTokensByAccount() {
        // Act
        refreshTokenService.revokeAllTokensByAccount(testAccount);

        // Assert
        verify(refreshTokenRepository).revokeAllTokensByAccount(testAccount);
    }

    @Test
    @DisplayName("Should generate unique token values")
    void testGenerateUniqueTokens() {
        // Arrange
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());

        // Act
        refreshTokenService.createRefreshToken(testAccount);
        refreshTokenService.createRefreshToken(testAccount);

        // Assert
        verify(refreshTokenRepository, times(2)).save(tokenCaptor.capture());
        
        String token1 = tokenCaptor.getAllValues().get(0).getToken();
        String token2 = tokenCaptor.getAllValues().get(1).getToken();
        
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2, "Generated tokens should be unique");
    }

    @Test
    @DisplayName("Should set correct expiration date")
    void testTokenExpirationDate() {
        // Arrange
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder().build());
        LocalDateTime beforeCreation = LocalDateTime.now();

        // Act
        refreshTokenService.createRefreshToken(testAccount);

        // Assert
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken savedToken = tokenCaptor.getValue();
        
        LocalDateTime expectedExpiry = beforeCreation.plusSeconds(REFRESH_TOKEN_EXPIRATION);
        LocalDateTime actualExpiry = savedToken.getExpiryDate();
        
        assertTrue(actualExpiry.isAfter(expectedExpiry.minusSeconds(5)) && 
                   actualExpiry.isBefore(expectedExpiry.plusSeconds(5)),
                   "Expiry date should be approximately " + REFRESH_TOKEN_EXPIRATION + " seconds from now");
    }
}
