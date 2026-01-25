package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.Account.AccountRequestDto;
import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.Service.Account.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Controller Test")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountRequestDto testAccountRequest;
    private AccountResponseDto testAccountResponse;
    private UUID testAccountId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();

        testAccountRequest = AccountRequestDto.builder()
                .name("Test User")
                .email("test@example.com")
                .mobile("1234567890")
                .linkedinUrl("https://linkedin.com/in/testuser")
                .githubUrl("https://github.com/testuser")
                .leetcodeUrl("https://leetcode.com/testuser")
                .cvPath("/path/to/cv.pdf")
                .aimRole("Software Engineer")
                .currentRole("Junior Developer")
                .build();

        testAccountResponse = AccountResponseDto.builder()
                .id(testAccountId)
                .name("Test User")
                .email("test@example.com")
                .mobile("1234567890")
                .linkedinUrl("https://linkedin.com/in/testuser")
                .githubUrl("https://github.com/testuser")
                .leetcodeUrl("https://leetcode.com/testuser")
                .cvPath("/path/to/cv.pdf")
                .aimRole("Software Engineer")
                .currentRole("Junior Developer")
                .totalRewardPoints(100)
                .taskAssignedCount(10)
                .taskCompletedCount(8)
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        // Arrange
        ApiResponse expectedResponse = ApiResponse.success("Account created successfully", true);
        when(accountService.createProfile(testAccountRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse> response = accountController.createUser(testAccountRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Account created successfully"));

        verify(accountService).createProfile(testAccountRequest);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserSuccess() {
        // Arrange
        when(accountService.getUser(testAccountId)).thenReturn(testAccountResponse);

        // Act
        ResponseEntity<AccountResponseDto> response = accountController.getUser(testAccountId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAccountResponse.getId(), response.getBody().getId());
        assertEquals(testAccountResponse.getName(), response.getBody().getName());
        assertEquals(testAccountResponse.getEmail(), response.getBody().getEmail());

        verify(accountService).getUser(testAccountId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent user")
    void testGetUserNotFound() {
        // Arrange
        when(accountService.getUser(testAccountId)).thenThrow(new RuntimeException("User not found with ID: " + testAccountId));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountController.getUser(testAccountId);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(accountService).getUser(testAccountId);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsersSuccess() {
        // Arrange
        AccountResponseDto user2 = AccountResponseDto.builder()
                .id(UUID.randomUUID())
                .name("User 2")
                .email("user2@example.com")
                .mobile("9876543210")
                .totalRewardPoints(50)
                .taskAssignedCount(5)
                .taskCompletedCount(3)
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        List<AccountResponseDto> expectedUsers = Arrays.asList(testAccountResponse, user2);
        when(accountService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        ResponseEntity<List<AccountResponseDto>> response = accountController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(testAccountResponse.getId(), response.getBody().get(0).getId());
        assertEquals(user2.getId(), response.getBody().get(1).getId());

        verify(accountService).getAllUsers();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        // Arrange
        when(accountService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<AccountResponseDto>> response = accountController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(accountService).getAllUsers();
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUserSuccess() {
        // Arrange
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .currentRole("Mid-level Developer")
                .build();

        ApiResponse expectedResponse = ApiResponse.success("User profile updated successfully", true);
        when(accountService.updateProfile(testAccountId, updates)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse> response = accountController.updateUser(testAccountId, updates);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("User profile updated successfully"));

        verify(accountService).updateProfile(testAccountId, updates);
    }

    @Test
    @DisplayName("Should return bad request when update fails")
    void testUpdateUserFailure() {
        // Arrange
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .build();

        ApiResponse failedResponse = ApiResponse.error("Update failed", "UPDATE_FAILED", "/api/v1/auth/account/update/" + testAccountId);
        when(accountService.updateProfile(testAccountId, updates)).thenReturn(failedResponse);

        // Act
        ResponseEntity<ApiResponse> response = accountController.updateUser(testAccountId, updates);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());

        verify(accountService).updateProfile(testAccountId, updates);
    }

    @Test
    @DisplayName("Should handle partial updates")
    void testUpdateUserPartial() {
        // Arrange
        AccountRequestDto partialUpdates = AccountRequestDto.builder()
                .mobile("5555555555")
                .build();

        ApiResponse expectedResponse = ApiResponse.success("User profile updated successfully", true);
        when(accountService.updateProfile(testAccountId, partialUpdates)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ApiResponse> response = accountController.updateUser(testAccountId, partialUpdates);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        verify(accountService).updateProfile(testAccountId, partialUpdates);
    }

    @Test
    @DisplayName("Should propagate service exceptions")
    void testCreateUserServiceException() {
        // Arrange
        when(accountService.createProfile(any(AccountRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Email already exists"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.createUser(testAccountRequest);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(accountService).createProfile(testAccountRequest);
    }

    @Test
    @DisplayName("Should propagate service exceptions on get all")
    void testGetAllUsersServiceException() {
        // Arrange
        when(accountService.getAllUsers()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountController.getAllUsers();
        });

        assertTrue(exception.getMessage().contains("Database connection failed"));
        verify(accountService).getAllUsers();
    }

    @Test
    @DisplayName("Should propagate service exceptions on update")
    void testUpdateUserServiceException() {
        // Arrange
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .build();

        when(accountService.updateProfile(testAccountId, updates))
                .thenThrow(new RuntimeException("User not found with ID: " + testAccountId));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountController.updateUser(testAccountId, updates);
        });

        assertTrue(exception.getMessage().contains("User not found"));
        verify(accountService).updateProfile(testAccountId, updates);
    }
}
