package com.learn.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    @Autowired
    private MessageProducer messageProducer;

    @Value("${kafka.topic.name}")
    private String topicName;

    @PostMapping("/produce")
    public ResponseEntity<String> sendMessage(@RequestParam("content") String content) {
        try {
            messageProducer.sendMessage(topicName, content);
            System.out.println("Message envoyé sur Kafka : " + content);
            return ResponseEntity.ok("✅ Message envoyé : " + content);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi : " + e.getMessage());
            return ResponseEntity.status(500).body("❌ Erreur lors de l'envoi : " + e.getMessage());
        }
    }
}