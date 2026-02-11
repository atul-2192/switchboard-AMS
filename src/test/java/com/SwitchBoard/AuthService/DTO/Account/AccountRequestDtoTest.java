package com.SwitchBoard.AuthService.DTO.Account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Account Request DTO Test")
class AccountRequestDtoTest {

    @Test
    @DisplayName("Should create account request with builder")
    void testBuilder() {
        // Arrange
        Date deadline = new Date();

        // Act
        AccountRequestDto dto = AccountRequestDto.builder()
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
        AccountRequestDto dto = new AccountRequestDto();
        Date deadline = new Date();

        // Act
        dto.setName("New User");
        dto.setEmail("new@example.com");
        dto.setMobile("9876543210");
        dto.setDeadline(deadline);
        dto.setAimRole("Tech Lead");
        dto.setCurrentRole("Senior Dev");

        // Assert
        assertEquals("New User", dto.getName());
        assertEquals("new@example.com", dto.getEmail());
        assertEquals("9876543210", dto.getMobile());
        assertEquals(deadline, dto.getDeadline());
        assertEquals("Tech Lead", dto.getAimRole());
        assertEquals("Senior Dev", dto.getCurrentRole());
    }

    @Test
    @DisplayName("Should support all-args constructor")
    void testAllArgsConstructor() {
        // Arrange
        Date deadline = new Date();

        // Act
        AccountRequestDto dto = new AccountRequestDto(
                "Test", "test@test.com", "123456", "linkedin", "github",
                "leetcode", "cv", deadline, "Engineer", "Dev",
                50, 5, 3, Collections.singletonList(USER_ROLE.ADMIN)
        );

        // Assert
        assertEquals("Test", dto.getName());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals(Collections.singletonList(USER_ROLE.ADMIN), dto.getUserRole());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void testNullFields() {
        // Act
        AccountRequestDto dto = AccountRequestDto.builder()
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
        assertNotNull(dto);
        assertNull(dto.getMobile());
        assertNull(dto.getLinkedinUrl());
        assertNull(dto.getGithubUrl());
    }

    @Test
    @DisplayName("Should handle ADMIN role")
    void testAdminRole() {
        // Act
        AccountRequestDto dto = AccountRequestDto.builder()
                .name("Admin")
                .email("admin@test.com")
                .userRole(Collections.singletonList(USER_ROLE.ADMIN))
                .build();

        // Assert
        assertEquals(USER_ROLE.ADMIN, dto.getUserRole().get(0));
    }
}
