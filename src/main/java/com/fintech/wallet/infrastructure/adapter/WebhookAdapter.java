package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.WebhookPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Webhook adapter for sending notifications to third-party integrations.
 */
@Component
public class WebhookAdapter implements WebhookPort {

    private static final Logger log = LoggerFactory.getLogger(WebhookAdapter.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;

    public WebhookAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    @Async
    public boolean sendWebhook(String webhookUrl, String eventType, Map<String, Object> payload) {
        try {
            Map<String, Object> webhookPayload = new HashMap<>(payload);
            webhookPayload.put("eventType", eventType);
            webhookPayload.put("timestamp", Instant.now().toString());

            webClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(webhookPayload)
                .retrieve()
                .toBodilessEntity()
                .block(TIMEOUT);

            log.info("Webhook sent successfully to {} for event {}", webhookUrl, eventType);
            return true;
        } catch (Exception e) {
            log.error("Failed to send webhook to {}: {}", webhookUrl, e.getMessage());
            return false;
        }
    }

    @Override
    @Async
    public boolean sendWebhookWithRetry(String webhookUrl, String eventType, 
            Map<String, Object> payload, int maxRetries) {
        int attempt = 0;
        while (attempt < maxRetries) {
            attempt++;
            if (sendWebhook(webhookUrl, eventType, payload)) {
                return true;
            }
            
            if (attempt < maxRetries) {
                try {
                    // Exponential backoff: 1s, 2s, 4s, etc.
                    Thread.sleep((long) Math.pow(2, attempt - 1) * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        
        log.error("Webhook delivery failed after {} attempts to {}", maxRetries, webhookUrl);
        return false;
    }
}
