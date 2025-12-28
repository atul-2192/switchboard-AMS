package com.SwitchBoard.AuthService.DTO.GoogleAuth;

import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleAuthResponse {
    private String accessToken;
    private String refreshToken;
    private boolean newUser;
    private AccountResponseDto user;
}