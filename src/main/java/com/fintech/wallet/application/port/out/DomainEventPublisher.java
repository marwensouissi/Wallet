package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.event.DomainEvent;

/**
 * Output port for publishing domain events.
 * Implemented by infrastructure to integrate with Spring Events.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event to all registered listeners.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);
}
