package com.SwitchBoard.AuthService.Util;

import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Util Test")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private static final long JWT_EXPIRATION = 3600; // 1 hour in seconds

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        
        // Generate RSA key pair for testing
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        
        // Set the private key in base64 format
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        ReflectionTestUtils.setField(jwtUtil, "privateKeyPath", privateKeyBase64);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", JWT_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token with all claims")
    void testGenerateToken() throws Exception {
        // Arrange
        String email = "test@example.com";
        String username = "Test User";
        UUID userId = UUID.randomUUID();
        var userRole = Collections.singletonList(USER_ROLE.USER);

        // Act
        String token = jwtUtil.generateToken(email, username, userId, userRole);

        // Assert
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");

        // Verify token structure
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts (header.payload.signature)");

        // Parse and verify claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject(), "Subject should match email");
        assertEquals(username, claims.get("username"), "Username claim should match");
        assertEquals(userId.toString(), claims.get("userId"), "UserId claim should match");
        assertNotNull(claims.get("role"), "Role claim should exist");
        assertNotNull(claims.getIssuedAt(), "Issued at should be set");
        assertNotNull(claims.getExpiration(), "Expiration should be set");
    }

    @Test
    @DisplayName("Should set correct expiration time")
    void testTokenExpiration() throws Exception {
        // Arrange
        String email = "test@example.com";
        String username = "Test User";
        UUID userId = UUID.randomUUID();
        var userRole = Collections.singletonList(USER_ROLE.USER);

        // Act
        String token = jwtUtil.generateToken(email, username, userId, userRole);

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        // Verify expiration is approximately JWT_EXPIRATION seconds after issued date
        long expirationDiff = expiration.getTime() - issuedAt.getTime();
        long expectedExpiration = JWT_EXPIRATION * 1000; // Convert to milliseconds
        
        assertTrue(Math.abs(expirationDiff - expectedExpiration) < 1000, 
                "Expiration should be approximately " + JWT_EXPIRATION + " seconds from issuance");

        // Verify issued at is recent (within last 5 seconds)
        long now = System.currentTimeMillis();
        assertTrue(Math.abs(now - issuedAt.getTime()) < 5000,
                "Issued at should be recent");
    }

    @Test
    @DisplayName("Should generate token with ADMIN role")
    void testGenerateTokenWithAdminRole() throws Exception {
        // Arrange
        String email = "admin@example.com";
        String username = "Admin User";
        UUID userId = UUID.randomUUID();
        var userRole = Collections.singletonList(USER_ROLE.ADMIN);

        // Act
        String token = jwtUtil.generateToken(email, username, userId, userRole);

        // Assert
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertNotNull(claims.get("role"));
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testTokenUniqueness() throws Exception {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // Act
        String token1 = jwtUtil.generateToken("user1@example.com", "User 1", userId1, Collections.singletonList(USER_ROLE.USER));
        String token2 = jwtUtil.generateToken("user2@example.com", "User 2", userId2, Collections.singletonList(USER_ROLE.USER));

        // Assert
        assertNotEquals(token1, token2, "Tokens for different users should be different");
    }

    @Test
    @DisplayName("Should throw exception when private key is invalid")
    void testInvalidPrivateKey() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtil, "privateKeyPath", "invalid-base64-key");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.generateToken("test@example.com", "Test User", UUID.randomUUID(), 
                    Collections.singletonList(USER_ROLE.USER));
        }, "Should throw exception for invalid private key");
    }

    @Test
    @DisplayName("Should handle special characters in username and email")
    void testSpecialCharacters() throws Exception {
        // Arrange
        String email = "test+special@example.com";
        String username = "Test User's Name";
        UUID userId = UUID.randomUUID();
        var userRole = Collections.singletonList(USER_ROLE.USER);

        // Act
        String token = jwtUtil.generateToken(email, username, userId, userRole);

        // Assert
        assertNotNull(token);
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertEquals(username, claims.get("username"));
    }
}
