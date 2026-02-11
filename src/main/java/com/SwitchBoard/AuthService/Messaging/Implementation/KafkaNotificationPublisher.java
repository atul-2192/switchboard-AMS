package com.SwitchBoard.AuthService.Messaging.Implementation;

import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import switchboard.schemas.OTPNotificationEvent;
import switchboard.schemas.OnboardingEvent;

@Service
@RequiredArgsConstructor
@Profile("dev")
@Slf4j
public class KafkaNotificationPublisher implements NotificationPublisher {

    private final KafkaTemplate<String, OTPNotificationEvent> otpKafkaTemplate;
    private final KafkaTemplate<String, OnboardingEvent> onboardingKafkaTemplate;

    @Value("${notification.otp.topic}")
    private String otpTopic;

    @Value("${notification.onboarding.topic}")
    private String onboardingTopic;

    @Override
    public void sendOtpNotification(String email, String otp) {
        OTPNotificationEvent event = new OTPNotificationEvent(email, otp);
        otpKafkaTemplate.send(otpTopic, event);
    }

    @Override
    public void sendOnboardingNotification(String email, String fullName) {
        OnboardingEvent event = new OnboardingEvent(email, fullName);
        onboardingKafkaTemplate.send(onboardingTopic, event);
    }
}