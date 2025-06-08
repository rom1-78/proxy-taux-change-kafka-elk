package com.learn.kafka.controller;

import com.learn.kafka.producer.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private MessageProducer messageProducer;

    @Value("${kafka.topic.name}")
    private String topicName;

    @GetMapping("/fetch")
    public String fetchRatesAndPublish(@RequestParam(defaultValue = "USD") String base) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + base;
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        // Envoi dans Kafka
        messageProducer.sendMessage(topicName, response);

        return "Taux récupérés et envoyés sur Kafka : " + response;
    }

}
