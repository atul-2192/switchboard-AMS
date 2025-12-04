package com.SwitchBoard.AuthService.Messaging.Publisher;

public interface NotificationPublisher {

    void sendOtpNotification(String email, String otp);

    void sendOnboardingNotification(String email, String fullName);

}
