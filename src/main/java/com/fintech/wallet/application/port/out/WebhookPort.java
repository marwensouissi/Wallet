package com.fintech.wallet.application.port.out;

import java.util.Map;

/**
 * Output port for sending webhook notifications to third-party integrations.
 * Implemented by infrastructure adapters.
 */
public interface WebhookPort {

    /**
     * Sends a webhook notification to a registered URL.
     *
     * @param webhookUrl the URL to send the webhook to
     * @param eventType the type of event
     * @param payload the event payload
     * @return true if the webhook was successfully delivered
     */
    boolean sendWebhook(String webhookUrl, String eventType, Map<String, Object> payload);

    /**
     * Sends a webhook notification with retry on failure.
     *
     * @param webhookUrl the URL to send the webhook to
     * @param eventType the type of event
     * @param payload the event payload
     * @param maxRetries maximum number of retry attempts
     * @return true if the webhook was eventually delivered
     */
    boolean sendWebhookWithRetry(String webhookUrl, String eventType, 
            Map<String, Object> payload, int maxRetries);
}
