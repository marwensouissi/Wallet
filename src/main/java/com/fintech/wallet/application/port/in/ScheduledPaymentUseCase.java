package com.fintech.wallet.application.port.in;

import com.fintech.wallet.application.command.CreateScheduledPaymentCommand;
import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;

import java.util.List;

/**
 * Input port for scheduled payment use cases.
 */
public interface ScheduledPaymentUseCase {

    /**
     * Creates a new scheduled payment.
     */
    ScheduledPaymentId createScheduledPayment(CreateScheduledPaymentCommand command);

    /**
     * Gets a scheduled payment by ID.
     */
    ScheduledPayment getScheduledPayment(String paymentId);

    /**
     * Gets all scheduled payments for a wallet.
     */
    List<ScheduledPayment> getScheduledPaymentsForWallet(String walletId);

    /**
     * Pauses a scheduled payment.
     */
    void pauseScheduledPayment(String paymentId);

    /**
     * Resumes a paused scheduled payment.
     */
    void resumeScheduledPayment(String paymentId);

    /**
     * Cancels a scheduled payment.
     */
    void cancelScheduledPayment(String paymentId);

    /**
     * Executes all due scheduled payments.
     * Called by the scheduler.
     */
    void executeDuePayments();

    /**
     * Sends reminders for upcoming payments.
     * Called by the scheduler.
     */
    void sendPaymentReminders();
}
