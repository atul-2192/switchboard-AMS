package com.SwitchBoard.AuthService.Service.Account.impl;

import com.SwitchBoard.AuthService.DTO.Account.AccountRequestDto;
import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Implementation Test")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private AccountRequestDto testAccountRequestDto;
    private UUID testAccountId;

    @BeforeEach
    void setUp() {
        testAccountId = UUID.randomUUID();
        
        testAccount = Account.builder()
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
                .googleAccount(false)
                .build();

        testAccountRequestDto = AccountRequestDto.builder()
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
    }

    @Test
    @DisplayName("Should create profile successfully")
    void testCreateProfileSuccess() {
        // Arrange
        when(accountRepository.findByEmail(testAccountRequestDto.getEmail())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        ApiResponse response = accountService.createProfile(testAccountRequestDto);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("Account created successfully"));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertEquals(testAccountRequestDto.getName(), savedAccount.getName());
        assertEquals(testAccountRequestDto.getEmail().toLowerCase(), savedAccount.getEmail());
        assertEquals(testAccountRequestDto.getMobile(), savedAccount.getMobile());
        assertEquals(0, savedAccount.getTotalRewardPoints());
        assertEquals(0, savedAccount.getTaskAssignedCount());
        assertEquals(0, savedAccount.getTaskCompletedCount());
        assertTrue(savedAccount.isGoogleAccount());

        verify(notificationPublisher).sendOnboardingNotification(
                savedAccount.getEmail(), savedAccount.getName());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateProfileEmailExists() {
        // Arrange
        when(accountRepository.findByEmail(testAccountRequestDto.getEmail())).thenReturn(Optional.of(testAccount));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.createProfile(testAccountRequestDto);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(accountRepository, never()).save(any(Account.class));
        verify(notificationPublisher, never()).sendOnboardingNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should convert email to lowercase when creating profile")
    void testCreateProfileEmailLowercase() {
        // Arrange
        testAccountRequestDto.setEmail("Test@EXAMPLE.COM");
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.createProfile(testAccountRequestDto);

        // Assert
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(accountCaptor.capture());
        assertEquals("test@example.com", accountCaptor.getValue().getEmail());
    }

    @Test
    @DisplayName("Should update profile successfully")
    void testUpdateProfileSuccess() {
        // Arrange
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .currentRole("Mid-level Developer")
                .mobile("9876543210")
                .githubUrl("https://github.com/newurl")
                .linkedinUrl("https://linkedin.com/in/newurl")
                .leetcodeUrl("https://leetcode.com/newurl")
                .build();

        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        ApiResponse response = accountService.updateProfile(testAccountId, updates);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.getMessage().contains("User profile updated successfully"));

        verify(accountRepository).findById(testAccountId);
        verify(accountRepository).save(testAccount);

        assertEquals(updates.getAimRole(), testAccount.getAimRole());
        assertEquals(updates.getCurrentRole(), testAccount.getCurrentRole());
        assertEquals(updates.getMobile(), testAccount.getMobile());
        assertEquals(updates.getGithubUrl(), testAccount.getGithubUrl());
        assertEquals(updates.getLinkedinUrl(), testAccount.getLinkedinUrl());
        assertEquals(updates.getLeetcodeUrl(), testAccount.getLeetcodeUrl());
    }

    @Test
    @DisplayName("Should update only non-null fields")
    void testUpdateProfilePartialUpdate() {
        // Arrange
        String originalMobile = testAccount.getMobile();
        String originalGithub = testAccount.getGithubUrl();
        
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .build();

        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.updateProfile(testAccountId, updates);

        // Assert
        assertEquals(updates.getAimRole(), testAccount.getAimRole());
        assertEquals(originalMobile, testAccount.getMobile());
        assertEquals(originalGithub, testAccount.getGithubUrl());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void testUpdateProfileUserNotFound() {
        // Arrange
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Senior Developer")
                .build();

        when(accountRepository.findById(testAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.updateProfile(testAccountId, updates);
        });

        assertTrue(exception.getMessage().contains("User not found with ID"));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers() {
        // Arrange
        Account account2 = Account.builder()
                .id(UUID.randomUUID())
                .name("User 2")
                .email("user2@example.com")
                .mobile("1111111111")
                .totalRewardPoints(50)
                .taskAssignedCount(5)
                .taskCompletedCount(3)
                .userRole(Collections.singletonList(USER_ROLE.USER))
                .build();

        List<Account> accounts = Arrays.asList(testAccount, account2);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<AccountResponseDto> result = accountService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        AccountResponseDto dto1 = result.get(0);
        assertEquals(testAccount.getId(), dto1.getId());
        assertEquals(testAccount.getName(), dto1.getName());
        assertEquals(testAccount.getEmail(), dto1.getEmail());
        assertEquals(testAccount.getMobile(), dto1.getMobile());
        assertEquals(testAccount.getTotalRewardPoints(), dto1.getTotalRewardPoints());

        AccountResponseDto dto2 = result.get(1);
        assertEquals(account2.getId(), dto2.getId());
        assertEquals(account2.getName(), dto2.getName());

        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void testGetAllUsersEmpty() {
        // Arrange
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<AccountResponseDto> result = accountService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(accountRepository).findAll();
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserSuccess() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));

        // Act
        AccountResponseDto result = accountService.getUser(testAccountId);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        assertEquals(testAccount.getName(), result.getName());
        assertEquals(testAccount.getEmail(), result.getEmail());
        assertEquals(testAccount.getMobile(), result.getMobile());
        assertEquals(testAccount.getLinkedinUrl(), result.getLinkedinUrl());
        assertEquals(testAccount.getGithubUrl(), result.getGithubUrl());
        assertEquals(testAccount.getLeetcodeUrl(), result.getLeetcodeUrl());
        assertEquals(testAccount.getCvPath(), result.getCvPath());
        assertEquals(testAccount.getAimRole(), result.getAimRole());
        assertEquals(testAccount.getCurrentRole(), result.getCurrentRole());
        assertEquals(testAccount.getTotalRewardPoints(), result.getTotalRewardPoints());
        assertEquals(testAccount.getTaskAssignedCount(), result.getTaskAssignedCount());
        assertEquals(testAccount.getTaskCompletedCount(), result.getTaskCompletedCount());

        verify(accountRepository).findById(testAccountId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent user")
    void testGetUserNotFound() {
        // Arrange
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.getUser(testAccountId);
        });

        assertTrue(exception.getMessage().contains("User not found with ID"));
        verify(accountRepository).findById(testAccountId);
    }

    @Test
    @DisplayName("Should handle repository exceptions during create")
    void testCreateProfileRepositoryException() {
        // Arrange
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            accountService.createProfile(testAccountRequestDto);
        });

        verify(notificationPublisher, never()).sendOnboardingNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should update all fields when all are provided")
    void testUpdateProfileAllFields() {
        // Arrange
        Date newDeadline = new Date();
        AccountRequestDto updates = AccountRequestDto.builder()
                .aimRole("Tech Lead")
                .currentRole("Senior Developer")
                .mobile("5555555555")
                .githubUrl("https://github.com/updated")
                .linkedinUrl("https://linkedin.com/in/updated")
                .leetcodeUrl("https://leetcode.com/updated")
                .deadline(newDeadline)
                .build();

        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        accountService.updateProfile(testAccountId, updates);

        // Assert
        assertEquals(updates.getAimRole(), testAccount.getAimRole());
        assertEquals(updates.getCurrentRole(), testAccount.getCurrentRole());
        assertEquals(updates.getMobile(), testAccount.getMobile());
        assertEquals(updates.getGithubUrl(), testAccount.getGithubUrl());
        assertEquals(updates.getLinkedinUrl(), testAccount.getLinkedinUrl());
        assertEquals(updates.getLeetcodeUrl(), testAccount.getLeetcodeUrl());
        assertEquals(newDeadline, testAccount.getDeadline());
    }
}
