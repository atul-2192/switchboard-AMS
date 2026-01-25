package com.SwitchBoard.AuthService.Service;

import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.DTO.Authentication.AuthResponse;
import com.SwitchBoard.AuthService.Exception.ResourceNotFoundException;
import com.SwitchBoard.AuthService.Exception.UnauthorizedException;
import com.SwitchBoard.AuthService.Exception.UnexpectedException;
import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Model.RefreshToken;
import com.SwitchBoard.AuthService.Repository.AccountRepository;
import com.SwitchBoard.AuthService.Util.JwtUtil;
import com.SwitchBoard.AuthService.Util.OtpUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OTP Service Test")
@MockitoSettings(strictness = Strictness.LENIENT)
class OtpServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private OtpService otpService;

    private Account testAccount;
    private static final String OTP_PREFIX = "otp:";
    private static final String COOLDOWN_PREFIX = "cooldown:";
    private static final int OTP_TTL_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 60;
    private static final int MAX_ATTEMPTS = 3;
    private static final Long JWT_EXPIRATION = 3600L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "OTP_PREFIX", OTP_PREFIX);
        ReflectionTestUtils.setField(otpService, "COOLDOWN_PREFIX", COOLDOWN_PREFIX);
        ReflectionTestUtils.setField(otpService, "OTP_TTL_MINUTES", OTP_TTL_MINUTES);
        ReflectionTestUtils.setField(otpService, "COOLDOWN_SECONDS", COOLDOWN_SECONDS);
        ReflectionTestUtils.setField(otpService, "MAX_ATTEMPTS", MAX_ATTEMPTS);
        ReflectionTestUtils.setField(otpService, "jwtExpiration", JWT_EXPIRATION);

        testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("Test User")
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Should generate OTP successfully")
    void testGenerateOtpSuccess() {
        // Arrange
        String email = "test@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(redisTemplate.hasKey(COOLDOWN_PREFIX + email.toLowerCase())).thenReturn(false);

        // Act
        ApiResponse response = otpService.generateOtp(email);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("OTP sent successfully"));

        verify(accountRepository).findByEmail(email);
        verify(redisTemplate).delete(OTP_PREFIX + email.toLowerCase());
        verify(hashOperations).put(eq(OTP_PREFIX + email.toLowerCase()), eq("hash"), anyString());
        verify(hashOperations).put(eq(OTP_PREFIX + email.toLowerCase()), eq("attempts"), eq(0));
        verify(redisTemplate).expire(OTP_PREFIX + email.toLowerCase(), OTP_TTL_MINUTES, TimeUnit.MINUTES);
        verify(valueOperations).set(eq(COOLDOWN_PREFIX + email.toLowerCase()), eq("1"), any(Duration.class));
        verify(notificationPublisher).sendOtpNotification(eq(email), anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testGenerateOtpUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            otpService.generateOtp(email);
        });

        assertTrue(exception.getMessage().contains("User with email " + email + " not found"));
        verify(accountRepository).findByEmail(email);
        verify(redisTemplate, never()).delete(anyString());
        verify(notificationPublisher, never()).sendOtpNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when cooldown period is active")
    void testGenerateOtpCooldownActive() {
        // Arrange
        String email = "test@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(redisTemplate.hasKey(COOLDOWN_PREFIX + email.toLowerCase())).thenReturn(true);

        // Act & Assert
        UnexpectedException exception = assertThrows(UnexpectedException.class, () -> {
            otpService.generateOtp(email);
        });

        assertTrue(exception.getMessage().contains("Please wait before requesting a new OTP"));
        verify(accountRepository).findByEmail(email);
        verify(redisTemplate, never()).delete(anyString());
        verify(notificationPublisher, never()).sendOtpNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should validate OTP successfully and return auth tokens")
    void testValidateOtpSuccess() throws Exception {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        String hashedOtp = OtpUtils.hashOtp(otp);
        String jwtToken = "jwt-token";
        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(true);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "hash")).thenReturn(hashedOtp);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "attempts")).thenReturn(0);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(jwtUtil.generateToken(email, testAccount.getName(), testAccount.getId(), testAccount.getUserRole()))
                .thenReturn(jwtToken);
        when(refreshTokenService.createRefreshToken(testAccount)).thenReturn(refreshToken);

        // Act
        AuthResponse response = otpService.validateOtp(email, otp);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getAccessToken());
        assertEquals(refreshToken.getToken(), response.getRefreshToken());
        assertEquals(JWT_EXPIRATION, response.getExpiresIn());

        verify(redisTemplate).delete(OTP_PREFIX + email.toLowerCase());
        verify(jwtUtil).generateToken(email, testAccount.getName(), testAccount.getId(), testAccount.getUserRole());
        verify(refreshTokenService).createRefreshToken(testAccount);
    }

    @Test
    @DisplayName("Should throw exception when OTP not found or expired")
    void testValidateOtpNotFound() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            otpService.validateOtp(email, otp);
        });

        assertTrue(exception.getMessage().contains("OTP expired or not found"));
        verify(accountRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw exception when max attempts exceeded")
    void testValidateOtpMaxAttemptsExceeded() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";

        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(true);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "hash")).thenReturn("hashed-otp");
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "attempts")).thenReturn(MAX_ATTEMPTS);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            otpService.validateOtp(email, otp);
        });

        assertTrue(exception.getMessage().contains("Maximum attempts exceeded"));
        verify(redisTemplate).delete(OTP_PREFIX + email.toLowerCase());
        verify(accountRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should increment attempts on invalid OTP")
    void testValidateOtpInvalidIncrementsAttempts() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        String correctHashedOtp = OtpUtils.hashOtp("654321");
        int currentAttempts = 1;

        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(true);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "hash")).thenReturn(correctHashedOtp);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "attempts")).thenReturn(currentAttempts);

        // Act & Assert
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            otpService.validateOtp(email, otp);
        });

        assertTrue(exception.getMessage().contains("Invalid OTP"));
        verify(hashOperations).put(OTP_PREFIX + email.toLowerCase(), "attempts", currentAttempts + 1);
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("Should handle null attempts gracefully")
    void testValidateOtpNullAttempts() throws Exception {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        String hashedOtp = OtpUtils.hashOtp(otp);
        String jwtToken = "jwt-token";
        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(true);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "hash")).thenReturn(hashedOtp);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "attempts")).thenReturn(null);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(jwtUtil.generateToken(email, testAccount.getName(), testAccount.getId(), testAccount.getUserRole()))
                .thenReturn(jwtToken);
        when(refreshTokenService.createRefreshToken(testAccount)).thenReturn(refreshToken);

        // Act
        AuthResponse response = otpService.validateOtp(email, otp);

        // Assert
        assertNotNull(response);
        verify(redisTemplate).delete(OTP_PREFIX + email.toLowerCase());
    }

    @Test
    @DisplayName("Should throw exception when user not found after OTP validation")
    void testValidateOtpUserNotFoundAfterValidation() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        String hashedOtp = OtpUtils.hashOtp(otp);

        when(redisTemplate.hasKey(OTP_PREFIX + email.toLowerCase())).thenReturn(true);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "hash")).thenReturn(hashedOtp);
        when(hashOperations.get(OTP_PREFIX + email.toLowerCase(), "attempts")).thenReturn(0);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            otpService.validateOtp(email, otp);
        });

        assertTrue(exception.getMessage().contains("User with email " + email + " not found"));
        verify(redisTemplate).delete(OTP_PREFIX + email.toLowerCase());
    }

    @Test
    @DisplayName("Should handle email case insensitivity")
    void testGenerateOtpEmailCaseInsensitive() {
        // Arrange
        String email = "Test@Example.COM";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(redisTemplate.hasKey(COOLDOWN_PREFIX + email.toLowerCase())).thenReturn(false);

        // Act
        ApiResponse response = otpService.generateOtp(email);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate).delete(keyCaptor.capture());
        assertTrue(keyCaptor.getValue().contains(email.toLowerCase()));
    }

    @Test
    @DisplayName("Should set correct TTL for OTP")
    void testGenerateOtpCorrectTTL() {
        // Arrange
        String email = "test@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(redisTemplate.hasKey(COOLDOWN_PREFIX + email.toLowerCase())).thenReturn(false);

        // Act
        otpService.generateOtp(email);

        // Assert
        verify(redisTemplate).expire(OTP_PREFIX + email.toLowerCase(), OTP_TTL_MINUTES, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("Should set correct cooldown duration")
    void testGenerateOtpCorrectCooldown() {
        // Arrange
        String email = "test@example.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(testAccount));
        when(redisTemplate.hasKey(COOLDOWN_PREFIX + email.toLowerCase())).thenReturn(false);

        // Act
        otpService.generateOtp(email);

        // Assert
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(valueOperations).set(eq(COOLDOWN_PREFIX + email.toLowerCase()), eq("1"), durationCaptor.capture());
        assertEquals(COOLDOWN_SECONDS, durationCaptor.getValue().getSeconds());
    }
}
