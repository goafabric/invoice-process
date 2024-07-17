package org.goafabric.invoice.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public NewTopic patient() {
        return TopicBuilder.name("patient").build();
    }

    @Bean
    public NewTopic chargeItem() {
        return TopicBuilder.name("chargeitem").build();
    }

    @Bean
    public NewTopic condition() {
        return TopicBuilder.name("condition").build();
    }
}
