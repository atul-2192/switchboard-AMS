package com.SwitchBoard.AuthService.Controller;

import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthRequest;
import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthResponse;
import com.SwitchBoard.AuthService.Service.GoogleAuth.GoogleAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Google Authentication", description = "Google OAuth 2.0 authentication endpoints")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @Operation(
        summary = "Login with Google",
        description = "Authenticates user with Google ID token received from frontend. " +
                     "Verifies the token with Google, creates/retrieves user account, " +
                     "and returns JWT access token and refresh token."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated with Google",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = GoogleAuthResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid Google ID token",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error during Google authentication",
            content = @Content
        )
    })
    @PostMapping("/google/login")
    public ResponseEntity<GoogleAuthResponse> googleLogin(@RequestBody GoogleAuthRequest request) {
        log.info("GoogleAuthController : googleLogin : Received Google login request");
        GoogleAuthResponse response = googleAuthService.loginWithGoogle(request.getIdToken());
        log.info("GoogleAuthController : googleLogin : Google login successful");
        return ResponseEntity.ok(response);
    }
}