package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.WebhookPort;
import com.fintech.wallet.domain.event.DomainEvent;
import com.fintech.wallet.domain.event.MoneyDepositedEvent;
import com.fintech.wallet.domain.event.MoneyTransferredEvent;
import com.fintech.wallet.domain.event.MoneyWithdrawnEvent;
import com.fintech.wallet.domain.event.WalletCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event listener for sending webhooks to registered third-party integrations.
 */
@Component
public class WebhookEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebhookEventListener.class);

    private final WebhookPort webhookPort;
    private final List<String> webhookUrls;
    private final boolean enabled;

    public WebhookEventListener(
            WebhookPort webhookPort,
            @Value("${webhook.urls:}") List<String> webhookUrls,
            @Value("${webhook.enabled:false}") boolean enabled) {
        this.webhookPort = webhookPort;
        this.webhookUrls = webhookUrls;
        this.enabled = enabled;
    }

    @EventListener
    @Async
    public void onMoneyDeposited(MoneyDepositedEvent event) {
        if (!enabled || webhookUrls.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("walletId", event.getWalletId().toString());
        payload.put("amount", event.getAmount().getAmount().toPlainString());
        payload.put("currency", event.getAmount().getCurrency().getCode());
        payload.put("newBalance", event.getNewBalance().getAmount().toPlainString());
        payload.put("description", event.getDescription());

        sendToAllWebhooks(event, payload);
    }

    @EventListener
    @Async
    public void onMoneyWithdrawn(MoneyWithdrawnEvent event) {
        if (!enabled || webhookUrls.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("walletId", event.getWalletId().toString());
        payload.put("amount", event.getAmount().getAmount().toPlainString());
        payload.put("currency", event.getAmount().getCurrency().getCode());
        payload.put("newBalance", event.getNewBalance().getAmount().toPlainString());
        payload.put("description", event.getDescription());

        sendToAllWebhooks(event, payload);
    }

    @EventListener
    @Async
    public void onMoneyTransferred(MoneyTransferredEvent event) {
        if (!enabled || webhookUrls.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("transactionId", event.getTransactionId().toString());
        payload.put("sourceWalletId", event.getSourceWalletId().toString());
        payload.put("destinationWalletId", event.getDestinationWalletId().toString());
        payload.put("amount", event.getAmount().getAmount().toPlainString());
        payload.put("currency", event.getAmount().getCurrency().getCode());
        payload.put("description", event.getDescription());
        payload.put("crossCurrency", event.isCrossCurrency());
        
        if (event.isCrossCurrency() && event.getConvertedAmount() != null) {
            payload.put("convertedAmount", event.getConvertedAmount().getAmount().toPlainString());
            payload.put("targetCurrency", event.getConvertedAmount().getCurrency().getCode());
        }

        sendToAllWebhooks(event, payload);
    }

    @EventListener
    @Async
    public void onWalletCreated(WalletCreatedEvent event) {
        if (!enabled || webhookUrls.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("walletId", event.getWalletId().toString());
        payload.put("currency", event.getCurrency());

        sendToAllWebhooks(event, payload);
    }

    private void sendToAllWebhooks(DomainEvent event, Map<String, Object> payload) {
        payload.put("eventId", event.getEventId().toString());
        payload.put("occurredAt", event.getOccurredAt().toString());

        for (String webhookUrl : webhookUrls) {
            if (!webhookUrl.isBlank()) {
                log.debug("Sending webhook to {} for event {}", webhookUrl, event.getEventType());
                webhookPort.sendWebhookWithRetry(webhookUrl, event.getEventType(), payload, 3);
            }
        }
    }
}
