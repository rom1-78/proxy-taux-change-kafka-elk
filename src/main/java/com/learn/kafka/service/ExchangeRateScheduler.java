package com.learn.kafka.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.learn.kafka.producer.MessageProducer;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ExchangeRateScheduler {

    @Autowired
    private MessageProducer messageProducer;

    @Value("${kafka.topic.name}")
    private String topicName;

    // URL de base de l’API
    @Value("${exchange.api.url}")
    private String apiUrl;

    // Appel toutes les 10 minutes (600_000 ms)
    @Scheduled(fixedRate = 60_000)
    public void fetchAndPublishRates() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            messageProducer.sendMessage(topicName, response);
            System.out.println("[Scheduler] Message envoyé sur Kafka : " + response);

        } catch (Exception e) {
            System.err.println("[Scheduler] Erreur lors de l’envoi : " + e.getMessage());
        }
    }
}
