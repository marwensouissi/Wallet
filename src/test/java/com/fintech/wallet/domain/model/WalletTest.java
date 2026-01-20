package com.fintech.wallet.domain.model;

import com.fintech.wallet.domain.exception.InsufficientBalanceException;
import com.fintech.wallet.domain.exception.InvalidCurrencyException;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Wallet aggregate.
 * No Spring context - pure domain tests.
 */
@DisplayName("Wallet Aggregate Tests")
class WalletTest {

    private Wallet wallet;
    private Currency usd;
    private TransactionId transactionId;

    @BeforeEach
    void setUp() {
        usd = Currency.of("USD");
        wallet = Wallet.create(usd);
        transactionId = TransactionId.generate();
    }

    @Test
    @DisplayName("Should create wallet with zero balance")
    void shouldCreateWalletWithZeroBalance() {
        assertThat(wallet.getId()).isNotNull();
        assertThat(wallet.getCurrency()).isEqualTo(usd);
        assertThat(wallet.calculateBalance().isZero()).isTrue();
        assertThat(wallet.getLedgerEntries()).isEmpty();
    }

    @Test
    @DisplayName("Should credit money to wallet")
    void shouldCreditMoneyToWallet() {
        Money amount = Money.of("100.00", "USD");

        LedgerEntry entry = wallet.credit(amount, transactionId, "Initial deposit");

        assertThat(entry).isNotNull();
        assertThat(entry.isCredit()).isTrue();
        assertThat(entry.getAmount()).isEqualTo(amount);
        assertThat(wallet.calculateBalance()).isEqualTo(amount);
        assertThat(wallet.getLedgerEntries()).hasSize(1);
    }

    @Test
    @DisplayName("Should debit money from wallet with sufficient balance")
    void shouldDebitMoneyFromWalletWithSufficientBalance() {
        Money creditAmount = Money.of("100.00", "USD");
        Money debitAmount = Money.of("30.00", "USD");

        wallet.credit(creditAmount, transactionId, "Deposit");
        LedgerEntry debitEntry = wallet.debit(debitAmount, transactionId, "Withdrawal");

        assertThat(debitEntry).isNotNull();
        assertThat(debitEntry.isDebit()).isTrue();
        assertThat(wallet.calculateBalance()).isEqualTo(Money.of("70.00", "USD"));
        assertThat(wallet.getLedgerEntries()).hasSize(2);
    }

    @Test
    @DisplayName("Should throw exception when debiting more than balance")
    void shouldThrowExceptionWhenDebitingMoreThanBalance() {
        Money creditAmount = Money.of("100.00", "USD");
        Money debitAmount = Money.of("150.00", "USD");

        wallet.credit(creditAmount, transactionId, "Deposit");

        assertThatThrownBy(() -> wallet.debit(debitAmount, transactionId, "Withdrawal"))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    @DisplayName("Should throw exception when crediting different currency")
    void shouldThrowExceptionWhenCreditingDifferentCurrency() {
        Money eurAmount = Money.of("100.00", "EUR");

        assertThatThrownBy(() -> wallet.credit(eurAmount, transactionId, "Deposit"))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessageContaining("does not match");
    }

    @Test
    @DisplayName("Should throw exception when debiting different currency")
    void shouldThrowExceptionWhenDebitingDifferentCurrency() {
        Money usdAmount = Money.of("100.00", "USD");
        Money eurAmount = Money.of("50.00", "EUR");

        wallet.credit(usdAmount, transactionId, "Deposit");

        assertThatThrownBy(() -> wallet.debit(eurAmount, transactionId, "Withdrawal"))
                .isInstanceOf(InvalidCurrencyException.class);
    }

    @Test
    @DisplayName("Should calculate balance from multiple ledger entries")
    void shouldCalculateBalanceFromMultipleLedgerEntries() {
        wallet.credit(Money.of("100.00", "USD"), transactionId, "Deposit 1");
        wallet.credit(Money.of("50.00", "USD"), transactionId, "Deposit 2");
        wallet.debit(Money.of("30.00", "USD"), transactionId, "Withdrawal 1");
        wallet.credit(Money.of("20.00", "USD"), transactionId, "Deposit 3");
        wallet.debit(Money.of("10.00", "USD"), transactionId, "Withdrawal 2");

        Money expectedBalance = Money.of("130.00", "USD");
        assertThat(wallet.calculateBalance()).isEqualTo(expectedBalance);
        assertThat(wallet.getLedgerEntries()).hasSize(5);
    }

    @Test
    @DisplayName("Should check if wallet can debit amount")
    void shouldCheckIfWalletCanDebitAmount() {
        Money balance = Money.of("100.00", "USD");
        wallet.credit(balance, transactionId, "Deposit");

        assertThat(wallet.canDebit(Money.of("50.00", "USD"))).isTrue();
        assertThat(wallet.canDebit(Money.of("100.00", "USD"))).isTrue();
        assertThat(wallet.canDebit(Money.of("150.00", "USD"))).isFalse();
    }

    @Test
    @DisplayName("Should return unmodifiable ledger entries list")
    void shouldReturnUnmodifiableLedgerEntriesList() {
        wallet.credit(Money.of("100.00", "USD"), transactionId, "Deposit");

        assertThatThrownBy(() -> wallet.getLedgerEntries().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Should maintain ledger entry immutability")
    void shouldMaintainLedgerEntryImmutability() {
        LedgerEntry entry = wallet.credit(Money.of("100.00", "USD"), transactionId, "Deposit");

        assertThat(entry.getId()).isNotNull();
        assertThat(entry.getWalletId()).isEqualTo(wallet.getId());
        assertThat(entry.getTransactionId()).isEqualTo(transactionId);
        assertThat(entry.getCreatedAt()).isNotNull();
    }
}
