package com.fintech.wallet.application.usecase;

import com.fintech.wallet.application.command.CreateScheduledPaymentCommand;
import com.fintech.wallet.application.command.TransferMoneyCommand;
import com.fintech.wallet.application.port.in.ScheduledPaymentUseCase;
import com.fintech.wallet.application.port.in.TransferMoneyUseCase;
import com.fintech.wallet.application.port.out.DomainEventPublisher;
import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.ScheduledPaymentPort;
import com.fintech.wallet.domain.exception.WalletNotFoundException;
import com.fintech.wallet.domain.model.RecurrencePattern;
import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.domain.valueobject.WalletId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Use case handler for scheduled payments.
 */
public class ScheduledPaymentUseCaseHandler implements ScheduledPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPaymentUseCaseHandler.class);

    private final ScheduledPaymentPort scheduledPaymentPort;
    private final LoadWalletPort loadWalletPort;
    private final TransferMoneyUseCase transferMoneyUseCase;
    private final DomainEventPublisher eventPublisher;

    public ScheduledPaymentUseCaseHandler(
            ScheduledPaymentPort scheduledPaymentPort,
            LoadWalletPort loadWalletPort,
            TransferMoneyUseCase transferMoneyUseCase,
            DomainEventPublisher eventPublisher) {
        this.scheduledPaymentPort = Objects.requireNonNull(scheduledPaymentPort);
        this.loadWalletPort = Objects.requireNonNull(loadWalletPort);
        this.transferMoneyUseCase = Objects.requireNonNull(transferMoneyUseCase);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public ScheduledPaymentId createScheduledPayment(CreateScheduledPaymentCommand command) {
        WalletId sourceWalletId = WalletId.of(command.getSourceWalletId());
        WalletId destinationWalletId = WalletId.of(command.getDestinationWalletId());

        // Validate wallets exist
        loadWalletPort.loadById(sourceWalletId)
                .orElseThrow(() -> new WalletNotFoundException(sourceWalletId.toString()));
        loadWalletPort.loadById(destinationWalletId)
                .orElseThrow(() -> new WalletNotFoundException(destinationWalletId.toString()));

        Currency currency = Currency.of(command.getCurrency());
        Money amount = Money.of(command.getAmount(), currency);
        RecurrencePattern pattern = RecurrencePattern.valueOf(command.getRecurrencePattern());

        ScheduledPayment payment;
        if (pattern == RecurrencePattern.ONCE) {
            payment = ScheduledPayment.createOneTime(
                    sourceWalletId,
                    destinationWalletId,
                    amount,
                    command.getDescription(),
                    command.getStartDate());
        } else {
            payment = ScheduledPayment.createRecurring(
                    sourceWalletId,
                    destinationWalletId,
                    amount,
                    command.getDescription(),
                    pattern,
                    command.getStartDate(),
                    command.getEndDate(),
                    command.getMaxExecutions());
        }

        scheduledPaymentPort.save(payment);
        log.info("Created scheduled payment: {}", payment.getId());

        return payment.getId();
    }

    @Override
    public ScheduledPayment getScheduledPayment(String paymentId) {
        ScheduledPaymentId id = ScheduledPaymentId.of(paymentId);
        return scheduledPaymentPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled payment not found: " + paymentId));
    }

    @Override
    public List<ScheduledPayment> getScheduledPaymentsForWallet(String walletId) {
        WalletId id = WalletId.of(walletId);
        return scheduledPaymentPort.findBySourceWalletId(id);
    }

    @Override
    public void pauseScheduledPayment(String paymentId) {
        ScheduledPaymentId id = ScheduledPaymentId.of(paymentId);
        ScheduledPayment payment = scheduledPaymentPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled payment not found: " + paymentId));

        ScheduledPayment paused = payment.pause();
        scheduledPaymentPort.save(paused);
        log.info("Paused scheduled payment: {}", paymentId);
    }

    @Override
    public void resumeScheduledPayment(String paymentId) {
        ScheduledPaymentId id = ScheduledPaymentId.of(paymentId);
        ScheduledPayment payment = scheduledPaymentPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled payment not found: " + paymentId));

        ScheduledPayment resumed = payment.resume();
        scheduledPaymentPort.save(resumed);
        log.info("Resumed scheduled payment: {}", paymentId);
    }

    @Override
    public void cancelScheduledPayment(String paymentId) {
        ScheduledPaymentId id = ScheduledPaymentId.of(paymentId);
        ScheduledPayment payment = scheduledPaymentPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scheduled payment not found: " + paymentId));

        ScheduledPayment cancelled = payment.cancel();
        scheduledPaymentPort.save(cancelled);
        log.info("Cancelled scheduled payment: {}", paymentId);
    }

    @Override
    public void executeDuePayments() {
        List<ScheduledPayment> duePayments = scheduledPaymentPort.findDuePayments(LocalDate.now());
        log.info("Found {} due scheduled payments", duePayments.size());

        for (ScheduledPayment payment : duePayments) {
            try {
                executePayment(payment);
            } catch (Exception e) {
                log.error("Failed to execute scheduled payment {}: {}", 
                        payment.getId(), e.getMessage());
                // Mark as failed but don't stop processing other payments
            }
        }
    }

    private void executePayment(ScheduledPayment payment) {
        log.info("Executing scheduled payment: {}", payment.getId());

        TransferMoneyCommand transferCommand = new TransferMoneyCommand(
                payment.getSourceWalletId().toString(),
                payment.getDestinationWalletId().toString(),
                payment.getAmount().getAmount(),
                payment.getAmount().getCurrency().getCode(),
                "Scheduled: " + payment.getDescription()
        );

        try {
            transferMoneyUseCase.execute(transferCommand);
            
            // Update payment with execution
            ScheduledPayment updated = payment.withExecution();
            scheduledPaymentPort.save(updated);
            
            log.info("Successfully executed scheduled payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Failed to execute scheduled payment {}: {}", payment.getId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void sendPaymentReminders() {
        // Send reminders for payments due in the next 2 days
        List<ScheduledPayment> upcomingPayments = scheduledPaymentPort.findUpcomingPayments(2);
        log.info("Sending reminders for {} upcoming payments", upcomingPayments.size());

        for (ScheduledPayment payment : upcomingPayments) {
            log.info("Payment reminder: {} scheduled for {} - {} {}", 
                    payment.getId(),
                    payment.getNextExecutionDate(),
                    payment.getAmount().getAmount(),
                    payment.getAmount().getCurrency());
            // In a real implementation, this would send email/push notifications
        }
    }
}
