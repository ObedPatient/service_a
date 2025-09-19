
/**
 * Configuration class for setting up Kafka producer beans.
 * @author Obed Patient
 * @version 1.0
 * @since 1.0
 */
package com.example.service_a.config;

import com.example.service_a.dto.AuditLogDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {

    /**
     * Creates a Kafka ProducerFactory for producing AuditLogDto messages.
     *
     * @return a configured ProducerFactory
     */
    @Bean
    public ProducerFactory<String, AuditLogDto> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, "true");
        configProps.put(JsonSerializer.TYPE_MAPPINGS, "auditLog:com.example.service_a.dto.AuditLogDto");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for sending AuditLogDto messages to Kafka.
     *
     * @return a configured KafkaTemplate
     */
    @Bean
    public KafkaTemplate<String, AuditLogDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}