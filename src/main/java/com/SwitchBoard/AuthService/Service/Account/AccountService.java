package com.SwitchBoard.AuthService.Service.Account;

import com.SwitchBoard.AuthService.DTO.Account.AccountRequestDto;
import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Authentication.ApiResponse;
import com.SwitchBoard.AuthService.Model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    public ApiResponse createProfile(AccountRequestDto account);
    public ApiResponse updateProfile(UUID accountId, AccountRequestDto updates);
    public List<AccountResponseDto> getAllUsers();
    public AccountResponseDto getUser(UUID id);
}
