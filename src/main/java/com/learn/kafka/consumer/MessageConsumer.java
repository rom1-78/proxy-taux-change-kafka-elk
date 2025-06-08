package com.learn.kafka.consumer;

import com.learn.kafka.elasticsearch.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {

    @Autowired
    private CurrencyRepository currencyRepository;

    @KafkaListener(topics = "${kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Message received: {}", message);

        // Indexation dans Elasticsearch
        currencyRepository.saveCurrencyRates(message);
    }
}
