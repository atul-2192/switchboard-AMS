package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthRequest;
import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthResponse;
import com.SwitchBoard.AuthService.Service.GoogleAuth.GoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google/login")
    public ResponseEntity<GoogleAuthResponse> googleLogin(@RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(googleAuthService.loginWithGoogle(request.getIdToken()));
    }
}