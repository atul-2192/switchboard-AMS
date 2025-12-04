package com.SwitchBoard.AuthService.Messaging.Implementation;

import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
@Slf4j
public class LocalNotificationPublisher implements NotificationPublisher {

    @Override
    public void sendOtpNotification(String email, String otp) {
        log.info("[LOCAL] OTP Notification -> email={}, otp={}", email, otp);
    }

    @Override
    public void sendOnboardingNotification(String email, String fullName) {
        log.info("[LOCAL] Onboarding Notification -> email={}, fullName={}", email, fullName);
    }
}