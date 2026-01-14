package com.SwitchBoard.AuthService.Messaging.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalMessagingConfig {
    // No beans required for logs-only implementation
}
