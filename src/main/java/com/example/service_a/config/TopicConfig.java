/**
 * Configuration class for defining Kafka topics.
 *
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class TopicConfig {

    /**
     * Creates a Kafka topic for audit logs.
     *
     * @return a new Kafka topic named "audit-log-topic"
     */
    @Bean
    public NewTopic auditLogTopic() {
        return TopicBuilder
                .name("audit-log-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    /**
     * Creates a Kafka topic for user signup events.
     *
     * @return a new Kafka topic named "user-signup-topic"
     */
    @Bean
    public NewTopic userSignupTopic() {
        return TopicBuilder
                .name("user-signup-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}