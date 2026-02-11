package com.SwitchBoard.AuthService.Service.Account.impl;


import com.SwitchBoard.AuthService.DTO.Account.AccountRequestDto;
import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Repository.AccountRepository;
import com.SwitchBoard.AuthService.Service.Account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final NotificationPublisher notificationPublisher;

    public ApiResponse createProfile(AccountRequestDto account) {
        log.info("AccountService : createProfile : Creating account for user - {}", account.getName());
        try {
            if (accountRepository.findByEmail(account.getEmail()).isPresent()) {
                log.warn("AccountService : createProfile : Email already exists - {}", account.getEmail());
                throw new IllegalArgumentException("Account creation failed: Email already exists");
            }
            Account newAccount = Account.builder()
                    .name(account.getName())
                    .email(account.getEmail().toLowerCase())
                    .mobile(account.getMobile())
                    .linkedinUrl(account.getLinkedinUrl())
                    .githubUrl(account.getGithubUrl())
                    .leetcodeUrl(account.getLeetcodeUrl())
                    .cvPath(account.getCvPath())
                    .deadline(account.getDeadline())
                    .aimRole(account.getAimRole())
                    .currentRole(account.getCurrentRole())
                    .totalRewardPoints(0)
                    .taskAssignedCount(0)
                    .taskCompletedCount(0)
                    .userRole(Collections.singletonList(USER_ROLE.USER))
                    .googleAccount(true).build();
            accountRepository.save(newAccount);
            notificationPublisher.sendOnboardingNotification(newAccount.getEmail(), newAccount.getName());
            log.info("AccountService : createProfile : Account created successfully - {}", account.getEmail());
            return ApiResponse.success("Account created successfully for " + account.getName(), true);
        } catch (IllegalArgumentException e) {
            log.error("AccountService : createProfile : IllegalArgumentException - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AccountService : createProfile : Unexpected error - {}", e.getMessage(), e);
            throw e;
        }
    }

    public ApiResponse updateProfile(UUID accountId, AccountRequestDto updates) {
        log.info("AccountService : updateProfile : Updating profile for user ID - {}", accountId);
        try {
            Account user = accountRepository.findById(accountId).orElseThrow(() -> {
                log.error("AccountService : updateProfile : User not found with ID - {}", accountId);
                return new RuntimeException("User not found with ID: " + accountId);
            });

            if(updates.getAimRole()!= null) {
                user.setAimRole(updates.getAimRole());
            }
            if(updates.getDeadline() != null) {
                user.setDeadline(updates.getDeadline());
            }
            if(updates.getCurrentRole() != null) {
                user.setCurrentRole(updates.getCurrentRole());
            }
            if(updates.getGithubUrl()!=null){
                user.setGithubUrl(updates.getGithubUrl());
            }
            if(updates.getLinkedinUrl()!=null){
                user.setLinkedinUrl(updates.getLinkedinUrl());
            }
            if(updates.getLeetcodeUrl()!=null){
                user.setLeetcodeUrl(updates.getLeetcodeUrl());
            }
            if(updates.getMobile()!= null) {
                user.setMobile(updates.getMobile());
            }

            accountRepository.save(user);
            log.info("AccountService : updateProfile : User profile updated successfully - {}", user.getName());

            return ApiResponse.success("User profile updated successfully for " + user.getName(), true);
        } catch (RuntimeException e) {
            log.error("AccountService : updateProfile : RuntimeException - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AccountService : updateProfile : Unexpected error - {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<AccountResponseDto> getAllUsers() {
        log.info("AccountService : getAllUsers : Retrieving all users from database");
        try {
            List<Account> accounts = accountRepository.findAll();

            List<AccountResponseDto> accountResponseDtos = accounts.stream()
                    .map(account -> AccountResponseDto.builder()
                            .id(account.getId())
                            .name(account.getName())
                            .email(account.getEmail())
                            .mobile(account.getMobile())
                            .linkedinUrl(account.getLinkedinUrl())
                            .githubUrl(account.getGithubUrl())
                            .leetcodeUrl(account.getLeetcodeUrl())
                            .cvPath(account.getCvPath())
                            .deadline(account.getDeadline())
                            .aimRole(account.getAimRole())
                            .currentRole(account.getCurrentRole())
                            .totalRewardPoints(account.getTotalRewardPoints())
                            .taskAssignedCount(account.getTaskAssignedCount())
                            .taskCompletedCount(account.getTaskCompletedCount())
                            .userRole(account.getUserRole())
                            .build())
                    .toList();

            log.info("AccountService : getAllUsers : Retrieved {} user accounts", accounts.size());
            return accountResponseDtos;
        } catch (Exception e) {
            log.error("AccountService : getAllUsers : Error retrieving all users - {}", e.getMessage(), e);
            throw e;
        }
    }

    public AccountResponseDto getUser(UUID id) {
        log.info("AccountService : getUser : Retrieving user with ID - {}", id);
        try {
            Account account = accountRepository.findById(id).orElseThrow(() -> {
                log.error("AccountService : getUser : User not found with ID - {}", id);
                return new RuntimeException("User not found with ID: " + id);
            });

            return AccountResponseDto.builder()
                    .id(account.getId())
                    .name(account.getName())
                    .email(account.getEmail())
                    .mobile(account.getMobile())
                    .linkedinUrl(account.getLinkedinUrl())
                    .githubUrl(account.getGithubUrl())
                    .leetcodeUrl(account.getLeetcodeUrl())
                    .cvPath(account.getCvPath())
                    .deadline(account.getDeadline())
                    .aimRole(account.getAimRole())
                    .currentRole(account.getCurrentRole())
                    .totalRewardPoints(account.getTotalRewardPoints())
                    .taskAssignedCount(account.getTaskAssignedCount())
                    .taskCompletedCount(account.getTaskCompletedCount())
                    .userRole(account.getUserRole())
                    .build();
        } catch (RuntimeException e) {
            log.error("AccountService : getUser : RuntimeException - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("AccountService : getUser : Unexpected error - {}", e.getMessage(), e);
            throw e;
        }
    }

}

