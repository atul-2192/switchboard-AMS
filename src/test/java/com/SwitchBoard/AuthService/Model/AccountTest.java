package com.SwitchBoard.AuthService.Model;

import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Model Test")
class AccountTest {

    @Test
    @DisplayName("Should create account with builder")
    void testAccountBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";
        String mobile = "1234567890";
        Date deadline = new Date();

        // Act
        Account account = Account.builder()
                .id(id)
                .name(name)
                .email(email)
                .mobile(mobile)
                .linkedinUrl("https://linkedin.com/in/testuser")
                .githubUrl("https://github.com/testuser")
                .leetcodeUrl("https://leetcode.com/testuser")
                .cvPath("/path/to/cv.pdf")
                .deadline(deadline)
                .aimRole("Software Engineer")
                .currentRole("Junior Developer")
                .totalRewardPoints(100)
                .taskAssignedCount(10)
                .taskCompletedCount(8)
                .googleId("google-id-123")
                .profileImageUrl("https://example.com/image.jpg")
                .googleAccount(true)
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        // Assert
        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals(name, account.getName());
        assertEquals(email, account.getEmail());
        assertEquals(mobile, account.getMobile());
        assertEquals("https://linkedin.com/in/testuser", account.getLinkedinUrl());
        assertEquals("https://github.com/testuser", account.getGithubUrl());
        assertEquals("https://leetcode.com/testuser", account.getLeetcodeUrl());
        assertEquals("/path/to/cv.pdf", account.getCvPath());
        assertEquals(deadline, account.getDeadline());
        assertEquals("Software Engineer", account.getAimRole());
        assertEquals("Junior Developer", account.getCurrentRole());
        assertEquals(100, account.getTotalRewardPoints());
        assertEquals(10, account.getTaskAssignedCount());
        assertEquals(8, account.getTaskCompletedCount());
        assertEquals("google-id-123", account.getGoogleId());
        assertEquals("https://example.com/image.jpg", account.getProfileImageUrl());
        assertTrue(account.isGoogleAccount());
        assertEquals(Collections.singletonList(USER_ROLE.USER), account.getUserRole());
    }

    @Test
    @DisplayName("Should create account with default values")
    void testAccountDefaults() {
        // Act
        Account account = Account.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        // Assert
        assertNotNull(account);
        assertEquals("Test User", account.getName());
        assertEquals("test@example.com", account.getEmail());
        assertEquals(0, account.getTotalRewardPoints());
        assertEquals(0, account.getTaskAssignedCount());
        assertEquals(0, account.getTaskCompletedCount());
    }

    @Test
    @DisplayName("Should support setters and getters")
    void testAccountSettersGetters() {
        // Arrange
        Account account = new Account();

        // Act
        account.setName("New Name");
        account.setEmail("new@example.com");
        account.setMobile("9876543210");
        account.setTotalRewardPoints(200);
        account.setTaskAssignedCount(20);
        account.setTaskCompletedCount(15);
        account.setGoogleAccount(false);

        // Assert
        assertEquals("New Name", account.getName());
        assertEquals("new@example.com", account.getEmail());
        assertEquals("9876543210", account.getMobile());
        assertEquals(200, account.getTotalRewardPoints());
        assertEquals(20, account.getTaskAssignedCount());
        assertEquals(15, account.getTaskCompletedCount());
        assertFalse(account.isGoogleAccount());
    }

    @Test
    @DisplayName("Should support ADMIN role")
    void testAccountWithAdminRole() {
        // Act
        Account account = Account.builder()
                .name("Admin User")
                .email("admin@example.com")
                .userRole(Collections.singletonList(USER_ROLE.ADMIN))
                .build();

        // Assert
        assertNotNull(account.getUserRole());
        assertEquals(1, account.getUserRole().size());
        assertEquals(USER_ROLE.ADMIN, account.getUserRole().get(0));
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testAccountWithNullFields() {
        // Act
        Account account = Account.builder()
                .name("Test User")
                .email("test@example.com")
                .mobile(null)
                .linkedinUrl(null)
                .githubUrl(null)
                .leetcodeUrl(null)
                .cvPath(null)
                .deadline(null)
                .aimRole(null)
                .currentRole(null)
                .googleId(null)
                .profileImageUrl(null)
                .build();

        // Assert
        assertNotNull(account);
        assertNull(account.getMobile());
        assertNull(account.getLinkedinUrl());
        assertNull(account.getGithubUrl());
        assertNull(account.getLeetcodeUrl());
        assertNull(account.getCvPath());
        assertNull(account.getDeadline());
        assertNull(account.getAimRole());
        assertNull(account.getCurrentRole());
        assertNull(account.getGoogleId());
        assertNull(account.getProfileImageUrl());
    }

    @Test
    @DisplayName("Should create account with Google authentication")
    void testGoogleAccount() {
        // Act
        Account account = Account.builder()
                .name("Google User")
                .email("googleuser@example.com")
                .googleId("google-oauth-id")
                .profileImageUrl("https://lh3.googleusercontent.com/a/default-user")
                .googleAccount(true)
                .build();

        // Assert
        assertTrue(account.isGoogleAccount());
        assertEquals("google-oauth-id", account.getGoogleId());
        assertEquals("https://lh3.googleusercontent.com/a/default-user", account.getProfileImageUrl());
    }

    @Test
    @DisplayName("Should update timestamps")
    void testAccountTimestamps() {
        // Arrange
        Account account = new Account();
        Date createdAt = new Date();
        Date updatedAt = new Date();

        // Act
        account.setCreatedAt(createdAt);
        account.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(createdAt, account.getCreatedAt());
        assertEquals(updatedAt, account.getUpdatedAt());
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        Date now = new Date();

        // Act
        Account account = new Account(
                id, "Test User", "test@example.com", "1234567890",
                "https://linkedin.com/in/test", "https://github.com/test",
                "https://leetcode.com/test", "/cv.pdf", now, "Engineer", 
                "Developer", 100, 10, 5, "google-id", "image-url", 
                true, Collections.singletonList(USER_ROLE.USER), 
                now, now
        );

        // Assert
        assertNotNull(account);
        assertEquals(id, account.getId());
        assertEquals("Test User", account.getName());
        assertEquals("test@example.com", account.getEmail());
    }

    @Test
    @DisplayName("Should support no-args constructor")
    void testNoArgsConstructor() {
        // Act
        Account account = new Account();

        // Assert
        assertNotNull(account);
    }
}
