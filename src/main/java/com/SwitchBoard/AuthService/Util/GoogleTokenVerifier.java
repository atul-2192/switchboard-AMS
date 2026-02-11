package com.SwitchBoard.AuthService.Util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Slf4j
public class GoogleTokenVerifier {

    @Value("${google.client.id}")
    private String googleClientId;

    /**
     * Verifies Google ID token and returns the payload
     * @param idTokenString - The Google ID token from frontend
     * @return GoogleIdToken.Payload containing user information
     * @throws RuntimeException if token verification fails
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    new GsonFactory()
            )
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                log.info("GoogleTokenVerifier : verify : Token verified successfully for email - {}", payload.getEmail());
                return payload;
            } else {
                log.error("GoogleTokenVerifier : verify : Token verification failed - token is null or invalid");
                return null;
            }

        } catch (Exception e) {
            log.error("GoogleTokenVerifier : verify : Error during token verification - {}", e.getMessage(), e);
            throw new RuntimeException("Google token verification failed: " + e.getMessage(), e);
        }
    }
}