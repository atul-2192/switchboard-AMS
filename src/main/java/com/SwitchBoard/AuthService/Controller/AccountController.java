package com.SwitchBoard.AuthService.Controller;


import com.SwitchBoard.AuthService.DTO.Account.AccountRequestDto;
import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Service.Account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/account")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "API endpoints for managing user accounts")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Create a new user account")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody AccountRequestDto accountRequestDto) {
            ApiResponse apiResponse = accountService.createProfile(accountRequestDto);
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/get/{id}")
    public ResponseEntity<AccountResponseDto> getUser(@PathVariable UUID id) {
        try {
            AccountResponseDto user = accountService.getUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("AccountController : getUser : Exception while retrieving user - {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Get all users")
    @GetMapping("/getAll")
    public ResponseEntity<List<AccountResponseDto>> getAllUsers() {
        try {
            List<AccountResponseDto> userDTOS = accountService.getAllUsers();
            return ResponseEntity.ok(userDTOS);
        } catch (Exception e) {
            log.error("AccountController : getAllUsers : Exception while retrieving all users - {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Update user")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable UUID id, @RequestBody AccountRequestDto updates) {
        try {
            ApiResponse apiResponse = accountService.updateProfile(id, updates);
            if (!apiResponse.isSuccess()) {
                return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("AccountController : updateUser : Exception while updating account - {}", e.getMessage(), e);
            throw e;
        }
    }
}

