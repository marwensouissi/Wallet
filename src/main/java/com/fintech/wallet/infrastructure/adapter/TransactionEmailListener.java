package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.EmailNotificationPort;
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

/**
 * Event listener for sending email notifications on domain events.
 */
@Component
public class TransactionEmailListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionEmailListener.class);

    private final EmailNotificationPort emailNotificationPort;
    private final String defaultNotificationEmail;

    public TransactionEmailListener(
            EmailNotificationPort emailNotificationPort,
            @Value("${notification.email.default-recipient:admin@wallet.fintech.com}") String defaultNotificationEmail) {
        this.emailNotificationPort = emailNotificationPort;
        this.defaultNotificationEmail = defaultNotificationEmail;
    }

    @EventListener
    @Async
    public void onMoneyDeposited(MoneyDepositedEvent event) {
        log.info("Processing MoneyDeposited event for wallet {}", event.getWalletId());

        String subject = String.format("Deposit Confirmation - %s %s",
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        
        String body = buildDepositEmailBody(event);
        emailNotificationPort.sendEmail(defaultNotificationEmail, subject, body);
    }

    @EventListener
    @Async
    public void onMoneyWithdrawn(MoneyWithdrawnEvent event) {
        log.info("Processing MoneyWithdrawn event for wallet {}", event.getWalletId());

        String subject = String.format("Withdrawal Confirmation - %s %s",
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        
        String body = buildWithdrawalEmailBody(event);
        emailNotificationPort.sendEmail(defaultNotificationEmail, subject, body);
    }

    @EventListener
    @Async
    public void onMoneyTransferred(MoneyTransferredEvent event) {
        log.info("Processing MoneyTransferred event for transaction {}", event.getTransactionId());

        String subject = String.format("Transfer Confirmation - %s %s",
                event.getAmount().getAmount(), event.getAmount().getCurrency());
        
        String body = buildTransferEmailBody(event);
        emailNotificationPort.sendEmail(defaultNotificationEmail, subject, body);
    }

    @EventListener
    @Async
    public void onWalletCreated(WalletCreatedEvent event) {
        log.info("Processing WalletCreated event for wallet {}", event.getWalletId());

        String subject = "New Wallet Created - " + event.getCurrency();
        String body = buildWalletCreatedEmailBody(event);
        emailNotificationPort.sendEmail(defaultNotificationEmail, subject, body);
    }

    private String buildDepositEmailBody(MoneyDepositedEvent event) {
        return String.format("""
            <html>
            <body>
                <h2>Deposit Confirmation</h2>
                <p>A deposit has been made to your wallet.</p>
                <table>
                    <tr><td><strong>Wallet ID:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Amount:</strong></td><td>%s %s</td></tr>
                    <tr><td><strong>New Balance:</strong></td><td>%s %s</td></tr>
                    <tr><td><strong>Description:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                </table>
            </body>
            </html>
            """,
            event.getWalletId(),
            event.getAmount().getAmount(), event.getAmount().getCurrency(),
            event.getNewBalance().getAmount(), event.getNewBalance().getCurrency(),
            event.getDescription(),
            event.getOccurredAt());
    }

    private String buildWithdrawalEmailBody(MoneyWithdrawnEvent event) {
        return String.format("""
            <html>
            <body>
                <h2>Withdrawal Confirmation</h2>
                <p>A withdrawal has been made from your wallet.</p>
                <table>
                    <tr><td><strong>Wallet ID:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Amount:</strong></td><td>%s %s</td></tr>
                    <tr><td><strong>New Balance:</strong></td><td>%s %s</td></tr>
                    <tr><td><strong>Description:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                </table>
            </body>
            </html>
            """,
            event.getWalletId(),
            event.getAmount().getAmount(), event.getAmount().getCurrency(),
            event.getNewBalance().getAmount(), event.getNewBalance().getCurrency(),
            event.getDescription(),
            event.getOccurredAt());
    }

    private String buildTransferEmailBody(MoneyTransferredEvent event) {
        String crossCurrencyInfo = event.isCrossCurrency() 
            ? String.format("<tr><td><strong>Converted Amount:</strong></td><td>%s %s</td></tr>",
                    event.getConvertedAmount().getAmount(), event.getConvertedAmount().getCurrency())
            : "";

        return String.format("""
            <html>
            <body>
                <h2>Transfer Confirmation</h2>
                <p>A transfer has been completed.</p>
                <table>
                    <tr><td><strong>Transaction ID:</strong></td><td>%s</td></tr>
                    <tr><td><strong>From Wallet:</strong></td><td>%s</td></tr>
                    <tr><td><strong>To Wallet:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Amount:</strong></td><td>%s %s</td></tr>
                    %s
                    <tr><td><strong>Description:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Date:</strong></td><td>%s</td></tr>
                </table>
            </body>
            </html>
            """,
            event.getTransactionId(),
            event.getSourceWalletId(),
            event.getDestinationWalletId(),
            event.getAmount().getAmount(), event.getAmount().getCurrency(),
            crossCurrencyInfo,
            event.getDescription(),
            event.getOccurredAt());
    }

    private String buildWalletCreatedEmailBody(WalletCreatedEvent event) {
        return String.format("""
            <html>
            <body>
                <h2>New Wallet Created</h2>
                <p>A new wallet has been created successfully.</p>
                <table>
                    <tr><td><strong>Wallet ID:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Currency:</strong></td><td>%s</td></tr>
                    <tr><td><strong>Created At:</strong></td><td>%s</td></tr>
                </table>
            </body>
            </html>
            """,
            event.getWalletId(),
            event.getCurrency(),
            event.getOccurredAt());
    }
}
