package com.fintech.wallet.infrastructure.scheduler;

import com.fintech.wallet.application.port.in.ScheduledPaymentUseCase;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Scheduler for executing scheduled payments and sending reminders.
 * Uses ShedLock for distributed locking in clustered environments.
 */
@Component
public class PaymentScheduler {

    private static final Logger log = LoggerFactory.getLogger(PaymentScheduler.class);

    private final ScheduledPaymentUseCase scheduledPaymentUseCase;
    private final boolean enabled;

    public PaymentScheduler(
            ScheduledPaymentUseCase scheduledPaymentUseCase,
            @Value("${scheduler.payments.enabled:true}") boolean enabled) {
        this.scheduledPaymentUseCase = scheduledPaymentUseCase;
        this.enabled = enabled;
    }

    /**
     * Executes due scheduled payments every hour.
     * Uses ShedLock to prevent concurrent execution in clustered environments.
     */
    @Scheduled(cron = "${scheduler.payments.cron:0 0 * * * *}")
    @SchedulerLock(name = "executeDuePayments", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void executeDuePayments() {
        if (!enabled) {
            log.debug("Payment scheduler disabled");
            return;
        }

        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("job", "executeDuePayments");

        log.info("Starting scheduled payment execution job");
        try {
            scheduledPaymentUseCase.executeDuePayments();
            log.info("Completed scheduled payment execution job");
        } catch (Exception e) {
            log.error("Error in scheduled payment execution: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Sends payment reminders every morning at 8 AM.
     * Uses ShedLock to prevent concurrent execution in clustered environments.
     */
    @Scheduled(cron = "${scheduler.reminders.cron:0 0 8 * * *}")
    @SchedulerLock(name = "sendPaymentReminders", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void sendPaymentReminders() {
        if (!enabled) {
            log.debug("Payment scheduler disabled");
            return;
        }

        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("job", "sendPaymentReminders");

        log.info("Starting payment reminder job");
        try {
            scheduledPaymentUseCase.sendPaymentReminders();
            log.info("Completed payment reminder job");
        } catch (Exception e) {
            log.error("Error in payment reminder job: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}
