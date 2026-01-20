package com.fintech.wallet.domain.valueobject;

import com.fintech.wallet.domain.exception.InvalidCurrencyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Money value object.
 * No Spring context - pure domain tests.
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    private static final Currency USD = Currency.of("USD");
    private static final Currency EUR = Currency.of("EUR");

    @Test
    @DisplayName("Should create money with valid amount and currency")
    void shouldCreateMoneyWithValidAmountAndCurrency() {
        Money money = Money.of(new BigDecimal("100.50"), USD);

        assertThat(money.getAmount()).isEqualByComparingTo("100.50");
        assertThat(money.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should create money from string amount")
    void shouldCreateMoneyFromStringAmount() {
        Money money = Money.of("50.25", "USD");

        assertThat(money.getAmount()).isEqualByComparingTo("50.25");
        assertThat(money.getCurrency()).isEqualTo(USD);
    }

    @Test
    @DisplayName("Should create zero money")
    void shouldCreateZeroMoney() {
        Money zero = Money.zero(USD);

        assertThat(zero.getAmount()).isEqualByComparingTo("0.00");
        assertThat(zero.isZero()).isTrue();
        assertThat(zero.isPositive()).isFalse();
    }

    @Test
    @DisplayName("Should add money with same currency")
    void shouldAddMoneyWithSameCurrency() {
        Money money1 = Money.of("100.00", "USD");
        Money money2 = Money.of("50.50", "USD");

        Money result = money1.add(money2);

        assertThat(result.getAmount()).isEqualByComparingTo("150.50");
    }

    @Test
    @DisplayName("Should subtract money with same currency")
    void shouldSubtractMoneyWithSameCurrency() {
        Money money1 = Money.of("100.00", "USD");
        Money money2 = Money.of("30.50", "USD");

        Money result = money1.subtract(money2);

        assertThat(result.getAmount()).isEqualByComparingTo("69.50");
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money usd = Money.of("100.00", "USD");
        Money eur = Money.of("50.00", "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessageContaining("USD")
                .hasMessageContaining("EUR");
    }

    @Test
    @DisplayName("Should throw exception when subtracting different currencies")
    void shouldThrowExceptionWhenSubtractingDifferentCurrencies() {
        Money usd = Money.of("100.00", "USD");
        Money eur = Money.of("50.00", "EUR");

        assertThatThrownBy(() -> usd.subtract(eur))
                .isInstanceOf(InvalidCurrencyException.class);
    }

    @Test
    @DisplayName("Should throw exception when creating negative money")
    void shouldThrowExceptionWhenCreatingNegativeMoney() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-10.00"), USD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    @DisplayName("Should throw exception when subtracting to negative result")
    void shouldThrowExceptionWhenSubtractingToNegativeResult() {
        Money money1 = Money.of("50.00", "USD");
        Money money2 = Money.of("100.00", "USD");

        assertThatThrownBy(() -> money1.subtract(money2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    @DisplayName("Should correctly compare money amounts")
    void shouldCorrectlyCompareMoneyAmounts() {
        Money money1 = Money.of("100.00", "USD");
        Money money2 = Money.of("50.00", "USD");
        Money money3 = Money.of("100.00", "USD");

        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isGreaterThan(money1)).isFalse();
        assertThat(money1.isGreaterThanOrEqual(money3)).isTrue();
        assertThat(money1.isGreaterThanOrEqual(money2)).isTrue();
    }

    @Test
    @DisplayName("Should round to 2 decimal places using HALF_UP")
    void shouldRoundToTwoDecimalPlacesUsingHalfUp() {
        Money money = Money.of(new BigDecimal("100.555"), USD);

        assertThat(money.getAmount()).isEqualByComparingTo("100.56");
    }

    @Test
    @DisplayName("Should consider equal money with same amount and currency")
    void shouldConsiderEqualMoneyWithSameAmountAndCurrency() {
        Money money1 = Money.of("100.00", "USD");
        Money money2 = Money.of("100.00", "USD");

        assertThat(money1).isEqualTo(money2);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
    }

    @Test
    @DisplayName("Should negate money amount")
    void shouldNegateMoneyAmount() {
        Money money = Money.of("100.00", "USD");
        Money negated = money.negate();

        assertThat(negated.getAmount()).isEqualByComparingTo("-100.00");
    }

    @Test
    @DisplayName("Should format money as string")
    void shouldFormatMoneyAsString() {
        Money money = Money.of("100.50", "USD");

        assertThat(money.toString()).isEqualTo("100.50 USD");
    }
}
