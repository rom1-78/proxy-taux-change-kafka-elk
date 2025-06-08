package com.learn.kafka.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.time.Instant;
import java.util.Map;

@Service
public class CurrencyRepository {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveCurrencyRates(String json) {
        try {
            // Parse le JSON en Map
            Map<String, Object> doc = objectMapper.readValue(json, Map.class);

            // Ajoute le champ timestamp
            doc.put("timestamp", Instant.now().toString());

            // Re-transforme la Map en JSON string
            String jsonWithTimestamp = objectMapper.writeValueAsString(doc);

            // Prépare la requête HTTP
            String url = "http://localhost:9200/exchange-rates/_doc";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonWithTimestamp, headers);

            // Envoie vers Elasticsearch
            restTemplate.postForEntity(url, entity, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
