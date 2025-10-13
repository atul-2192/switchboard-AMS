package com.SwitchBoard.AuthService.DTO.Authentication;

import lombok.Data;

@Data
public class AuthValidateRequest {
    private String email;
    private String otp;
}
