package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.DomainEventPublisher;
import com.fintech.wallet.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adapter that bridges domain events to Spring's event system.
 */
@Component
public class SpringEventPublisherAdapter implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringEventPublisherAdapter.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event: {} [{}]", event.getEventType(), event.getEventId());
        applicationEventPublisher.publishEvent(event);
    }
}
