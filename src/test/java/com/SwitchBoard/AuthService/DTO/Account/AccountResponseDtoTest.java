package com.SwitchBoard.AuthService.DTO.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Response DTO Test")
class AccountResponseDtoTest {

    @Test
    @DisplayName("Should create account response with builder")
    void testBuilder() {
        // Arrange
        UUID id = UUID.randomUUID();
        Date deadline = new Date();

        // Act
        AccountResponseDto dto = AccountResponseDto.builder()
                .id(id)
                .name("Test User")
                .email("test@example.com")
                .mobile("1234567890")
                .linkedinUrl("https://linkedin.com/in/test")
                .githubUrl("https://github.com/test")
                .leetcodeUrl("https://leetcode.com/test")
                .cvPath("/path/cv.pdf")
                .deadline(deadline)
                .aimRole("Software Engineer")
                .currentRole("Developer")
                .totalRewardPoints(100)
                .taskAssignedCount(10)
                .taskCompletedCount(8)
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        // Assert
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getMobile());
        assertEquals("https://linkedin.com/in/test", dto.getLinkedinUrl());
        assertEquals("https://github.com/test", dto.getGithubUrl());
        assertEquals("https://leetcode.com/test", dto.getLeetcodeUrl());
        assertEquals("/path/cv.pdf", dto.getCvPath());
        assertEquals(deadline, dto.getDeadline());
        assertEquals("Software Engineer", dto.getAimRole());
        assertEquals("Developer", dto.getCurrentRole());
        assertEquals(100, dto.getTotalRewardPoints());
        assertEquals(10, dto.getTaskAssignedCount());
        assertEquals(8, dto.getTaskCompletedCount());
        assertEquals(Collections.singletonList(USER_ROLE.USER), dto.getUserRole());
    }

    @Test
    @DisplayName("Should support no-args constructor and setters")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        AccountResponseDto dto = new AccountResponseDto();
        UUID id = UUID.randomUUID();

        // Act
        dto.setId(id);
        dto.setName("New User");
        dto.setEmail("new@example.com");
        dto.setTotalRewardPoints(200);
        dto.setTaskAssignedCount(20);
        dto.setTaskCompletedCount(15);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals("New User", dto.getName());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals(200, dto.getTotalRewardPoints());
        assertEquals(20, dto.getTaskAssignedCount());
        assertEquals(15, dto.getTaskCompletedCount());
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();
        Date deadline = new Date();

        // Act
        AccountResponseDto dto = new AccountResponseDto(
                id, "Test", "test@test.com", "123456", "linkedin", "github",
                "leetcode", "cv", deadline, "Engineer", "Dev",
                50, 5, 3, Collections.singletonList(USER_ROLE.ADMIN)
        );

        // Assert
        assertEquals(id, dto.getId());
        assertEquals("Test", dto.getName());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals(50, dto.getTotalRewardPoints());
    }

    @Test
    @DisplayName("Should handle zero reward points and task counts")
    void testZeroCounts() {
        // Act
        AccountResponseDto dto = AccountResponseDto.builder()
                .id(UUID.randomUUID())
                .name("New User")
                .email("new@test.com")
                .totalRewardPoints(0)
                .taskAssignedCount(0)
                .taskCompletedCount(0)
                .build();

        // Assert
        assertEquals(0, dto.getTotalRewardPoints());
        assertEquals(0, dto.getTaskAssignedCount());
        assertEquals(0, dto.getTaskCompletedCount());
    }

    @Test
    @DisplayName("Should handle high task counts")
    void testHighCounts() {
        // Act
        AccountResponseDto dto = AccountResponseDto.builder()
                .totalRewardPoints(10000)
                .taskAssignedCount(500)
                .taskCompletedCount(450)
                .build();

        // Assert
        assertEquals(10000, dto.getTotalRewardPoints());
        assertEquals(500, dto.getTaskAssignedCount());
        assertEquals(450, dto.getTaskCompletedCount());
    }

    @Test
    @DisplayName("Should calculate completion rate correctly")
    void testCompletionRate() {
        // Act
        AccountResponseDto dto = AccountResponseDto.builder()
                .taskAssignedCount(10)
                .taskCompletedCount(8)
                .build();

        // Assert
        double completionRate = (double) dto.getTaskCompletedCount() / dto.getTaskAssignedCount();
        assertEquals(0.8, completionRate, 0.01);
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullFields() {
        // Act
        AccountResponseDto dto = AccountResponseDto.builder()
                .id(UUID.randomUUID())
                .name("User")
                .email("user@test.com")
                .mobile(null)
                .linkedinUrl(null)
                .githubUrl(null)
                .leetcodeUrl(null)
                .cvPath(null)
                .deadline(null)
                .build();

        // Assert
        assertNotNull(dto.getId());
        assertNull(dto.getMobile());
        assertNull(dto.getLinkedinUrl());
        assertNull(dto.getGithubUrl());
    }

    @Test
    @DisplayName("Should differentiate between USER and ADMIN roles")
    void testDifferentRoles() {
        // Act
        AccountResponseDto user = AccountResponseDto.builder()
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        AccountResponseDto admin = AccountResponseDto.builder()
                .userRole(Collections.singletonList(USER_ROLE.ADMIN))
                .build();

        // Assert
        assertEquals(USER_ROLE.USER, user.getUserRole().get(0));
        assertEquals(USER_ROLE.ADMIN, admin.getUserRole().get(0));
        assertNotEquals(user.getUserRole(), admin.getUserRole());
    }
}
