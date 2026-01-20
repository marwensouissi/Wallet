package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Transaction entity.
 * No Spring context - pure domain tests.
 */
@DisplayName("Transaction Entity Tests")
class TransactionTest {

    @Test
    @DisplayName("Should create transfer transaction")
    void shouldCreateTransferTransaction() {
        WalletId sourceId = WalletId.generate();
        WalletId destinationId = WalletId.generate();
        Money amount = Money.of("100.00", "USD");

        Transaction transaction = Transaction.createTransfer(
                sourceId,
                destinationId,
                amount,
                "Payment for services");

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getSourceWalletId()).isEqualTo(sourceId);
        assertThat(transaction.getDestinationWalletId()).isEqualTo(destinationId);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getDescription()).isEqualTo("Payment for services");
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(transaction.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should use default description when null")
    void shouldUseDefaultDescriptionWhenNull() {
        Transaction transaction = Transaction.createTransfer(
                WalletId.generate(),
                WalletId.generate(),
                Money.of("50.00", "USD"),
                null);

        assertThat(transaction.getDescription()).isEqualTo("Transfer");
    }

    @Test
    @DisplayName("Should throw exception when source and destination are same")
    void shouldThrowExceptionWhenSourceAndDestinationAreSame() {
        WalletId walletId = WalletId.generate();
        Money amount = Money.of("100.00", "USD");

        assertThatThrownBy(() -> Transaction.builder()
                .id(TransactionId.generate())
                .sourceWalletId(walletId)
                .destinationWalletId(walletId)
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(java.time.Instant.now())
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be different");
    }

    @Test
    @DisplayName("Should enforce required fields")
    void shouldEnforceRequiredFields() {
        assertThatThrownBy(() -> Transaction.builder()
                .sourceWalletId(WalletId.generate())
                .destinationWalletId(WalletId.generate())
                .amount(Money.of("100.00", "USD"))
                .build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should be equal based on ID")
    void shouldBeEqualBasedOnId() {
        TransactionId id = TransactionId.generate();
        WalletId source = WalletId.generate();
        WalletId destination = WalletId.generate();
        Money amount = Money.of("100.00", "USD");

        Transaction tx1 = Transaction.builder()
                .id(id)
                .sourceWalletId(source)
                .destinationWalletId(destination)
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(java.time.Instant.now())
                .build();

        Transaction tx2 = Transaction.builder()
                .id(id)
                .sourceWalletId(source)
                .destinationWalletId(destination)
                .amount(amount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(java.time.Instant.now())
                .build();

        assertThat(tx1).isEqualTo(tx2);
        assertThat(tx1.hashCode()).isEqualTo(tx2.hashCode());
    }
}
