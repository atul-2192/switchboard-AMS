package com.SwitchBoard.AuthService.Messaging.Config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import switchboard.schemas.OTPNotificationEvent;
import switchboard.schemas.OnboardingEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("dev")
public class KafkaMessagingConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return config;
    }

    @Bean
    public ProducerFactory<String, OTPNotificationEvent> otpProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }

    @Bean
    public KafkaTemplate<String, OTPNotificationEvent> otpKafkaTemplate() {
        return new KafkaTemplate<>(otpProducerFactory());
    }

    @Bean
    public ProducerFactory<String, OnboardingEvent> onboardingProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }

    @Bean
    public KafkaTemplate<String, OnboardingEvent> onboardingKafkaTemplate() {
        return new KafkaTemplate<>(onboardingProducerFactory());
    }
}
