package com.fintech.wallet.domain.service;

import com.fintech.wallet.domain.exception.InsufficientBalanceException;
import com.fintech.wallet.domain.exception.InvalidCurrencyException;
import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for TransferDomainService.
 * No Spring context - pure domain tests.
 */
@DisplayName("Transfer Domain Service Tests")
class TransferDomainServiceTest {

    private TransferDomainService transferService;
    private Wallet sourceWallet;
    private Wallet destinationWallet;
    private Currency usd;
    private TransactionId transactionId;

    @BeforeEach
    void setUp() {
        transferService = new TransferDomainService();
        usd = Currency.of("USD");
        sourceWallet = Wallet.create(usd);
        destinationWallet = Wallet.create(usd);
        transactionId = TransactionId.generate();
        
        // Fund the source wallet
        sourceWallet.credit(Money.of("500.00", "USD"), transactionId, "Initial funding");
    }

    @Test
    @DisplayName("Should transfer money successfully between wallets")
    void shouldTransferMoneySuccessfully() {
        Money transferAmount = Money.of("100.00", "USD");
        
        TransferDomainService.TransferResult result = transferService.transfer(
                sourceWallet, 
                destinationWallet, 
                transferAmount, 
                "Test transfer");
        
        assertThat(result).isNotNull();
        assertThat(result.getTransaction()).isNotNull();
        assertThat(result.getSourceEntry()).isNotNull();
        assertThat(result.getDestinationEntry()).isNotNull();
        
        // Verify transaction
        Transaction transaction = result.getTransaction();
        assertThat(transaction.getSourceWalletId()).isEqualTo(sourceWallet.getId());
        assertThat(transaction.getDestinationWalletId()).isEqualTo(destinationWallet.getId());
        assertThat(transaction.getAmount()).isEqualTo(transferAmount);
        
        // Verify ledger entries
        assertThat(result.getSourceEntry().isDebit()).isTrue();
        assertThat(result.getDestinationEntry().isCredit()).isTrue();
        
        // Verify balances
        assertThat(sourceWallet.calculateBalance()).isEqualTo(Money.of("400.00", "USD"));
        assertThat(destinationWallet.calculateBalance()).isEqualTo(Money.of("100.00", "USD"));
    }

    @Test
    @DisplayName("Should throw exception when source has insufficient balance")
    void shouldThrowExceptionWhenInsufficientBalance() {
        Money largeAmount = Money.of("1000.00", "USD");
        
        assertThatThrownBy(() -> 
                transferService.transfer(sourceWallet, destinationWallet, largeAmount, null))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should throw exception when wallets have different currencies")
    void shouldThrowExceptionWhenDifferentCurrencies() {
        Currency eur = Currency.of("EUR");
        Wallet eurWallet = Wallet.create(eur);
        Money amount = Money.of("100.00", "USD");
        
        assertThatThrownBy(() -> 
                transferService.transfer(sourceWallet, eurWallet, amount, null))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessageContaining("does not match");
    }

    @Test
    @DisplayName("Should throw exception when amount currency differs from wallet currency")
    void shouldThrowExceptionWhenAmountCurrencyDiffers() {
        Money eurAmount = Money.of("100.00", "EUR");
        
        assertThatThrownBy(() -> 
                transferService.transfer(sourceWallet, destinationWallet, eurAmount, null))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessageContaining("does not match");
    }

    @Test
    @DisplayName("Should transfer exact wallet balance successfully")
    void shouldTransferExactBalance() {
        Money exactBalance = Money.of("500.00", "USD");
        
        TransferDomainService.TransferResult result = transferService.transfer(
                sourceWallet, 
                destinationWallet, 
                exactBalance, 
                "Transfer all");
        
        assertThat(result).isNotNull();
        assertThat(sourceWallet.calculateBalance()).isEqualTo(Money.of("0.00", "USD"));
        assertThat(destinationWallet.calculateBalance()).isEqualTo(Money.of("500.00", "USD"));
    }

    @Test
    @DisplayName("Should include description in ledger entries")
    void shouldIncludeDescriptionInLedgerEntries() {
        Money amount = Money.of("50.00", "USD");
        String description = "Payment for services";
        
        TransferDomainService.TransferResult result = transferService.transfer(
                sourceWallet, 
                destinationWallet, 
                amount, 
                description);
        
        assertThat(result.getSourceEntry().getDescription()).contains(description);
        assertThat(result.getDestinationEntry().getDescription()).contains(description);
    }

    @Test
    @DisplayName("Should throw NullPointerException for null source wallet")
    void shouldThrowExceptionForNullSourceWallet() {
        Money amount = Money.of("100.00", "USD");
        
        assertThatThrownBy(() -> 
                transferService.transfer(null, destinationWallet, amount, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Source wallet is required");
    }

    @Test
    @DisplayName("Should throw NullPointerException for null destination wallet")
    void shouldThrowExceptionForNullDestinationWallet() {
        Money amount = Money.of("100.00", "USD");
        
        assertThatThrownBy(() -> 
                transferService.transfer(sourceWallet, null, amount, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Destination wallet is required");
    }

    @Test
    @DisplayName("Should throw NullPointerException for null amount")
    void shouldThrowExceptionForNullAmount() {
        assertThatThrownBy(() -> 
                transferService.transfer(sourceWallet, destinationWallet, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Amount is required");
    }
}
