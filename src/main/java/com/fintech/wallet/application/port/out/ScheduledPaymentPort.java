package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.model.ScheduledPaymentStatus;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.domain.valueobject.WalletId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Output port for scheduled payment persistence.
 */
public interface ScheduledPaymentPort {

    /**
     * Saves a scheduled payment.
     */
    void save(ScheduledPayment payment);

    /**
     * Finds a scheduled payment by ID.
     */
    Optional<ScheduledPayment> findById(ScheduledPaymentId id);

    /**
     * Finds all scheduled payments for a wallet.
     */
    List<ScheduledPayment> findBySourceWalletId(WalletId walletId);

    /**
     * Finds all active scheduled payments due for execution.
     */
    List<ScheduledPayment> findDuePayments(LocalDate date);

    /**
     * Finds all scheduled payments by status.
     */
    List<ScheduledPayment> findByStatus(ScheduledPaymentStatus status);

    /**
     * Finds payments with upcoming execution for reminders.
     * Returns payments scheduled within the specified number of days.
     */
    List<ScheduledPayment> findUpcomingPayments(int daysAhead);

    /**
     * Deletes a scheduled payment.
     */
    void delete(ScheduledPaymentId id);
}
