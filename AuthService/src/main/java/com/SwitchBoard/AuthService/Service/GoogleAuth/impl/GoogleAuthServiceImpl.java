package com.SwitchBoard.AuthService.Service.GoogleAuth.impl;

import com.SwitchBoard.AuthService.DTO.Account.AccountResponseDto;
import com.SwitchBoard.AuthService.DTO.Account.USER_ROLE;
import com.SwitchBoard.AuthService.DTO.GoogleAuth.GoogleAuthResponse;
import com.SwitchBoard.AuthService.Model.Account;
import com.SwitchBoard.AuthService.Repository.AccountRepository;
import com.SwitchBoard.AuthService.Service.GoogleAuth.GoogleAuthService;
import com.SwitchBoard.AuthService.Util.GoogleTokenVerifier;
import com.SwitchBoard.AuthService.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @Override
    public GoogleAuthResponse loginWithGoogle(String idToken) {

        try {
            // 1. Verify Google Token
            var payload = googleTokenVerifier.verify(idToken);

            if (payload == null) {
                throw new RuntimeException("Invalid Google ID Token");
            }

            // 2. Extract required Google user data
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // 3. Check if user exists
            Account account = accountRepository.findByEmail(email).orElse(null);
            boolean newUser = false;

            // 4. Create new user if not exists
            if (account == null) {
                account = Account.builder()
                        .name(name)
                        .email(email)
                        .userRole(Collections.singletonList(USER_ROLE.USER))
                        .totalRewardPoints(0)
                        .taskAssignedCount(0)
                        .taskCompletedCount(0)
                        .googleAccount(true).build();

                accountRepository.save(account);
                newUser = true;
            }

            // 5. Generate JWT using YOUR JwtUtil
            String accessToken = jwtUtil.generateToken(
                    account.getEmail(),
                    account.getName(),
                    account.getId(),
                    account.getUserRole()
            );

            // 6. Return response
            return GoogleAuthResponse.builder()
                    .accessToken(accessToken)
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

        } catch (Exception e) {
            throw new RuntimeException("Google Login Failed: " + e.getMessage());
        }
    }
}
