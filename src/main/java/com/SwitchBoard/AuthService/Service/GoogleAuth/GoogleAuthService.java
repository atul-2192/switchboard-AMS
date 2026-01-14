package com.SwitchBoard.AuthService.Service.GoogleAuth;

import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthResponse;

public interface GoogleAuthService {
    GoogleAuthResponse loginWithGoogle(String idToken);
}
