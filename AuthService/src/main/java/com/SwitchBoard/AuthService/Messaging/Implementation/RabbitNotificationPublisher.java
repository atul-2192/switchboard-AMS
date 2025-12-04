package com.SwitchBoard.AuthService.Messaging.Implementation;

import com.SwitchBoard.AuthService.Messaging.Publisher.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import switchboard.schemas.OTPNotificationEvent;
import switchboard.schemas.OnboardingEvent;

@Service
@RequiredArgsConstructor
@Profile("prod")
@Slf4j
public class RabbitNotificationPublisher implements NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;


    @Value("${rabbitmq.queue.otp}")
    private String otpQueue;

    @Value("${rabbitmq.queue.onboarding}")
    private String onboardingQueue;


    @Override
    public void sendOtpNotification(String email, String otp) {
        OTPNotificationEvent event = new OTPNotificationEvent(email, otp);
        log.info("Publishing OTP event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(otpQueue, event);
    }

    @Override
    public void sendOnboardingNotification(String email, String fullName) {
        OnboardingEvent event = new OnboardingEvent(email, fullName);
        log.info("Publishing Onboarding event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(onboardingQueue, event);
    }
}