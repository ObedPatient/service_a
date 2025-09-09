package com.example.service_a.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Bean
    public NewTopic auditLogTopic() {
        return TopicBuilder
                .name("audit-log-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
