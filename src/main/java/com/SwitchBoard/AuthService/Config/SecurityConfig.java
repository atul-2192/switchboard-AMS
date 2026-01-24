package com.SwitchBoard.AuthService.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Permit all for auth endpoints and Swagger
                        .requestMatchers(
                                "/api/v1/auth/google/login",
                                "/api/v1/auth/**",
                                "/api/v1/auth/account/**",
                                "/.well-known/jwks.json",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/oauth2/**",
                                "/login/**",
                                "/login/oauth2/**",
                                "/api/v1/auth/**",
                                "/error",
                                "/actuator/health",
                                "/actuator/info"

                        ).permitAll()
                        .anyRequest().authenticated()  // everything else requires auth
                )
               .httpBasic(AbstractHttpConfigurer::disable)  // disable basic login popup
                .formLogin(AbstractHttpConfigurer::disable);           // disable default login form

        return http.build();
    }
}
