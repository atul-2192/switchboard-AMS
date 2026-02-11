package com.SwitchBoard.AuthService.Service.GoogleAuth.impl;

import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthResponse;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Model.RefreshToken;
import com.SwitchBoard.AuthService.Repository.AccountRepository;
import com.SwitchBoard.AuthService.Service.GoogleAuth.GoogleAuthService;
import com.SwitchBoard.AuthService.Service.RefreshTokenService;
import com.SwitchBoard.AuthService.Util.GoogleTokenVerifier;
import com.SwitchBoard.AuthService.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public GoogleAuthResponse loginWithGoogle(String idToken) {
        
        log.info("GoogleAuthServiceImpl : loginWithGoogle : Starting Google authentication");

        try {
            var payload = googleTokenVerifier.verify(idToken);

            if (payload == null) {
                log.error("GoogleAuthServiceImpl : loginWithGoogle : Invalid Google ID Token");
                throw new RuntimeException("Invalid Google ID Token");
            }

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            log.info("GoogleAuthServiceImpl : loginWithGoogle : Google token verified for email - {}", email);

            Account account = accountRepository.findByEmail(email).orElse(null);
            boolean newUser = false;

            if (account == null) {
                log.info("GoogleAuthServiceImpl : loginWithGoogle : Creating new account for email - {}", email);
                account = Account.builder()
                        .name(name)
                        .email(email)
                        .userRole(Collections.singletonList(USER_ROLE.USER))
                        .totalRewardPoints(0)
                        .taskAssignedCount(0)
                        .taskCompletedCount(0)
                        .googleAccount(true)
                        .build();

                accountRepository.save(account);
                newUser = true;
                log.info("GoogleAuthServiceImpl : loginWithGoogle : New account created successfully");
            }

            String accessToken = jwtUtil.generateToken(
                    account.getEmail(),
                    account.getName(),
                    account.getId(),
                    account.getUserRole()
            );

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(account);

            GoogleAuthResponse response = GoogleAuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .newUser(newUser)
                    .user(AccountResponseDto.builder()
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
                            .build()
                    )
                    .build();

            log.info("GoogleAuthServiceImpl : loginWithGoogle : Google login successful for email - {}", email);
            return response;

        } catch (Exception e) {
            log.error("GoogleAuthServiceImpl : loginWithGoogle : Google Login Failed - {}", e.getMessage(), e);
            throw new RuntimeException("Google Login Failed: " + e.getMessage(), e);
        }
    }
}
