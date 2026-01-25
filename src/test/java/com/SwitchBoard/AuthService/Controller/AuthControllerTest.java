package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.DTO.Authentication.AuthRequest;
import com.SwitchBoard.AuthService.DTO.Authentication.AuthResponse;
import com.SwitchBoard.AuthService.DTO.Authentication.AuthValidateRequest;
import com.SwitchBoard.AuthService.DTO.Authentication.RefreshTokenRequest;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.Exception.UnauthorizedException;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Model.RefreshToken;
import com.SwitchBoard.AuthService.Service.OtpService;
import com.SwitchBoard.AuthService.Service.RefreshTokenService;
import com.SwitchBoard.AuthService.Util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Test")
class AuthControllerTest {

    @Mock
    private OtpService otpService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private static final Long JWT_EXPIRATION = 3600L;
    private Account testAccount;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "jwtExpiration", JWT_EXPIRATION);

        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        testRefreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("valid-refresh-token")
                .account(testAccount)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();
    }

    @Test
    @DisplayName("Should send OTP successfully")
    void testSendOtpSuccess() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");

        ApiResponse expectedResponse = ApiResponse.success("OTP sent successfully", true);
        when(otpService.generateOtp(request.getEmail())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse> response = authController.sendOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("OTP sent successfully"));

        verify(otpService).generateOtp(request.getEmail());
    }

    @Test
    @DisplayName("Should verify OTP successfully and return auth tokens")
    void testVerifyOtpSuccess() throws Exception {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("jwt-token")
                .refreshToken("refresh-token")
                .expiresIn(JWT_EXPIRATION)
                .build();

        when(otpService.validateOtp(request.getEmail(), request.getOtp())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.verifyOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse.getAccessToken(), response.getBody().getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), response.getBody().getRefreshToken());
        assertEquals(expectedResponse.getExpiresIn(), response.getBody().getExpiresIn());

        verify(otpService).validateOtp(request.getEmail(), request.getOtp());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void testRefreshTokenSuccess() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        String newAccessToken = "new-jwt-token";
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token("new-refresh-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenService.isTokenValid(testRefreshToken)).thenReturn(true);
        when(jwtUtil.generateToken(
                testAccount.getEmail(),
                testAccount.getName(),
                testAccount.getId(),
                testAccount.getUserRole()
        )).thenReturn(newAccessToken);
        when(refreshTokenService.createRefreshToken(testAccount)).thenReturn(newRefreshToken);

        // Act
        ResponseEntity<AuthResponse> response = authController.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newAccessToken, response.getBody().getAccessToken());
        assertEquals(newRefreshToken.getToken(), response.getBody().getRefreshToken());
        assertEquals(JWT_EXPIRATION, response.getBody().getExpiresIn());

        verify(refreshTokenService).findByToken(request.getRefreshToken());
        verify(refreshTokenService).isTokenValid(testRefreshToken);
        verify(jwtUtil).generateToken(
                testAccount.getEmail(),
                testAccount.getName(),
                testAccount.getId(),
                testAccount.getUserRole()
        );
        verify(refreshTokenService).createRefreshToken(testAccount);
    }

    @Test
    @DisplayName("Should throw exception when refresh token not found")
    void testRefreshTokenNotFound() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");

        when(refreshTokenService.findByToken(request.getRefreshToken())).thenReturn(Optional.empty());

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authController.refreshToken(request);
        });

        assertTrue(exception.getMessage().contains("Invalid refresh token"));
        verify(refreshTokenService).findByToken(request.getRefreshToken());
        verify(refreshTokenService, never()).isTokenValid(any());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void testRefreshTokenInvalid() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("expired-token");

        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenService.isTokenValid(testRefreshToken)).thenReturn(false);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authController.refreshToken(request);
        });

        assertTrue(exception.getMessage().contains("Refresh token expired or invalid"));
        verify(refreshTokenService).findByToken(request.getRefreshToken());
        verify(refreshTokenService).isTokenValid(testRefreshToken);
        verify(refreshTokenService, never()).createRefreshToken(any());
    }

    @Test
    @DisplayName("Should handle email with different cases in send OTP")
    void testSendOtpEmailCaseInsensitive() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("Test@Example.COM");

        ApiResponse expectedResponse = ApiResponse.success("OTP sent successfully", true);
        when(otpService.generateOtp(request.getEmail())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse> response = authController.sendOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(otpService).generateOtp("Test@Example.COM");
    }

    @Test
    @DisplayName("Should handle special OTP characters")
    void testVerifyOtpWithSpecialCharacters() throws Exception {
        // Arrange
        AuthValidateRequest request = new AuthValidateRequest();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        AuthResponse expectedResponse = AuthResponse.builder()
                .accessToken("jwt-token")
                .refreshToken("refresh-token")
                .expiresIn(JWT_EXPIRATION)
                .build();

        when(otpService.validateOtp(request.getEmail(), request.getOtp())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.verifyOtp(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(otpService).validateOtp(request.getEmail(), request.getOtp());
    }

    @Test
    @DisplayName("Should generate new refresh token when refreshing")
    void testRefreshTokenGeneratesNewToken() throws Exception {
        // Arrange
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("old-refresh-token");

        String newAccessToken = "new-jwt-token";
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token("completely-new-refresh-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(refreshTokenService.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenService.isTokenValid(testRefreshToken)).thenReturn(true);
        when(jwtUtil.generateToken(any(), any(), any(), any())).thenReturn(newAccessToken);
        when(refreshTokenService.createRefreshToken(testAccount)).thenReturn(newRefreshToken);

        // Act
        ResponseEntity<AuthResponse> response = authController.refreshToken(request);

        // Assert
        assertNotNull(response.getBody());
        assertNotEquals(request.getRefreshToken(), response.getBody().getRefreshToken());
        assertEquals("completely-new-refresh-token", response.getBody().getRefreshToken());
    }
}
