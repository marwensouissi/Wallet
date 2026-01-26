package com.fintech.wallet.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.wallet.interfaces.rest.dto.CreateWalletRequest;
import com.fintech.wallet.interfaces.rest.dto.DepositRequest;
import com.fintech.wallet.interfaces.rest.dto.TransferMoneyRequest;
import com.fintech.wallet.interfaces.rest.dto.WithdrawRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Wallet REST API endpoints.
 * Tests complete request/response cycles with H2 in-memory database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Wallet API Integration Tests")
class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /api/wallets - Create Wallet")
    class CreateWalletTests {

        @Test
        @DisplayName("Should create a new USD wallet successfully")
        void shouldCreateNewWallet() throws Exception {
            CreateWalletRequest request = CreateWalletRequest.builder()
                    .currency("USD")
                    .build();

            mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.balance").value(0))
                    .andExpect(jsonPath("$.createdAt").isNotEmpty());
        }

        @Test
        @DisplayName("Should create a new EUR wallet successfully")
        void shouldCreateEurWallet() throws Exception {
            CreateWalletRequest request = CreateWalletRequest.builder()
                    .currency("EUR")
                    .build();

            mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.currency").value("EUR"))
                    .andExpect(jsonPath("$.balance").value(0));
        }

        @Test
        @DisplayName("Should reject invalid currency code")
        void shouldRejectInvalidCurrency() throws Exception {
            String invalidRequest = """
                    {"currency": "INVALID"}
                    """;

            mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject empty currency")
        void shouldRejectEmptyCurrency() throws Exception {
            String emptyRequest = """
                    {"currency": ""}
                    """;

            mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyRequest))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject missing currency field")
        void shouldRejectMissingCurrency() throws Exception {
            mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/wallets/{id} - Get Wallet")
    class GetWalletTests {

        @Test
        @DisplayName("Should retrieve an existing wallet")
        void shouldRetrieveExistingWallet() throws Exception {
            // First create a wallet
            CreateWalletRequest request = CreateWalletRequest.builder()
                    .currency("USD")
                    .build();

            MvcResult createResult = mockMvc.perform(post("/api/wallets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String walletId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                    .get("id").asText();

            // Then retrieve it
            mockMvc.perform(get("/api/wallets/{walletId}", walletId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(walletId))
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.balance").value(0));
        }

        @Test
        @DisplayName("Should return 404 for non-existent wallet")
        void shouldReturn404ForNonExistentWallet() throws Exception {
            mockMvc.perform(get("/api/wallets/{walletId}", "00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }

        @Test
        @DisplayName("Should return 400 for invalid wallet ID format")
        void shouldReturn400ForInvalidWalletIdFormat() throws Exception {
            mockMvc.perform(get("/api/wallets/{walletId}", "invalid-uuid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/transfers - Transfer Money")
    class TransferMoneyTests {

        @Test
        @DisplayName("Should transfer money between wallets successfully")
        void shouldTransferMoneySuccessfully() throws Exception {
            // Create source wallet
            String sourceWalletId = createWalletAndGetId("USD");

            // Create destination wallet
            String destWalletId = createWalletAndGetId("USD");

            // Fund source wallet (we need to add a deposit endpoint or seed data)
            // For now, this test will fail because source wallet has zero balance
            // This highlights a missing feature!

            TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                    .sourceWalletId(sourceWalletId)
                    .destinationWalletId(destWalletId)
                    .amount(new BigDecimal("50.00"))
                    .currency("USD")
                    .description("Test transfer")
                    .build();

            // This will fail with InsufficientBalanceException - expected!
            mockMvc.perform(post("/api/transfers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transferRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Insufficient Balance"));
        }

        @Test
        @DisplayName("Should reject transfer with missing source wallet")
        void shouldRejectTransferWithMissingSourceWallet() throws Exception {
            String destWalletId = createWalletAndGetId("USD");

            TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                    .sourceWalletId("00000000-0000-0000-0000-000000000000")
                    .destinationWalletId(destWalletId)
                    .amount(new BigDecimal("50.00"))
                    .currency("USD")
                    .description("Test transfer")
                    .build();

            mockMvc.perform(post("/api/transfers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transferRequest)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should reject transfer with negative amount")
        void shouldRejectNegativeAmount() throws Exception {
            String sourceWalletId = createWalletAndGetId("USD");
            String destWalletId = createWalletAndGetId("USD");

            TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                    .sourceWalletId(sourceWalletId)
                    .destinationWalletId(destWalletId)
                    .amount(new BigDecimal("-50.00"))
                    .currency("USD")
                    .description("Invalid transfer")
                    .build();

            mockMvc.perform(post("/api/transfers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transferRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject transfer with zero amount")
        void shouldRejectZeroAmount() throws Exception {
            String sourceWalletId = createWalletAndGetId("USD");
            String destWalletId = createWalletAndGetId("USD");

            TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                    .sourceWalletId(sourceWalletId)
                    .destinationWalletId(destWalletId)
                    .amount(BigDecimal.ZERO)
                    .currency("USD")
                    .description("Zero amount transfer")
                    .build();

            mockMvc.perform(post("/api/transfers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transferRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject transfer between wallets with different currencies")
        void shouldRejectCurrencyMismatch() throws Exception {
            String usdWalletId = createWalletAndGetId("USD");
            String eurWalletId = createWalletAndGetId("EUR");

            TransferMoneyRequest transferRequest = TransferMoneyRequest.builder()
                    .sourceWalletId(usdWalletId)
                    .destinationWalletId(eurWalletId)
                    .amount(new BigDecimal("50.00"))
                    .currency("USD")
                    .description("Cross-currency transfer")
                    .build();

            mockMvc.perform(post("/api/transfers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(transferRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid Currency"));
        }
    }

    @Nested
    @DisplayName("POST /api/wallets/{id}/deposit - Deposit Money")
    class DepositMoneyTests {

        @Test
        @DisplayName("Should deposit money into wallet successfully")
        void shouldDepositMoneySuccessfully() throws Exception {
            String walletId = createWalletAndGetId("USD");

            DepositRequest request = DepositRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .currency("USD")
                    .description("Initial deposit")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.ledgerEntryId").isNotEmpty())
                    .andExpect(jsonPath("$.walletId").value(walletId))
                    .andExpect(jsonPath("$.amount").value(100.00))
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.newBalance").value(100.00))
                    .andExpect(jsonPath("$.description").value("Initial deposit"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("Should accumulate multiple deposits")
        void shouldAccumulateMultipleDeposits() throws Exception {
            String walletId = createWalletAndGetId("USD");

            // First deposit
            DepositRequest deposit1 = DepositRequest.builder()
                    .amount(new BigDecimal("50.00"))
                    .currency("USD")
                    .description("Deposit 1")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deposit1)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.newBalance").value(50.00));

            // Second deposit
            DepositRequest deposit2 = DepositRequest.builder()
                    .amount(new BigDecimal("75.00"))
                    .currency("USD")
                    .description("Deposit 2")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deposit2)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.newBalance").value(125.00));
        }

        @Test
        @DisplayName("Should reject deposit with mismatched currency")
        void shouldRejectMismatchedCurrency() throws Exception {
            String walletId = createWalletAndGetId("USD");

            DepositRequest request = DepositRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .currency("EUR")
                    .description("Wrong currency deposit")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid Currency"));
        }

        @Test
        @DisplayName("Should reject deposit with zero amount")
        void shouldRejectZeroAmount() throws Exception {
            String walletId = createWalletAndGetId("USD");

            DepositRequest request = DepositRequest.builder()
                    .amount(BigDecimal.ZERO)
                    .currency("USD")
                    .description("Zero deposit")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 404 for non-existent wallet")
        void shouldReturn404ForNonExistentWallet() throws Exception {
            DepositRequest request = DepositRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .currency("USD")
                    .description("Deposit")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/deposit", "00000000-0000-0000-0000-000000000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/wallets/{id}/withdraw - Withdraw Money")
    class WithdrawMoneyTests {

        @Test
        @DisplayName("Should withdraw money from wallet successfully")
        void shouldWithdrawMoneySuccessfully() throws Exception {
            String walletId = createWalletAndGetId("USD");

            // First deposit money
            depositToWallet(walletId, new BigDecimal("100.00"), "USD");

            // Then withdraw
            WithdrawRequest request = WithdrawRequest.builder()
                    .amount(new BigDecimal("30.00"))
                    .currency("USD")
                    .description("ATM withdrawal")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/withdraw", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.ledgerEntryId").isNotEmpty())
                    .andExpect(jsonPath("$.walletId").value(walletId))
                    .andExpect(jsonPath("$.amount").value(30.00))
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.newBalance").value(70.00))
                    .andExpect(jsonPath("$.description").value("ATM withdrawal"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());
        }

        @Test
        @DisplayName("Should reject withdrawal exceeding balance")
        void shouldRejectWithdrawalExceedingBalance() throws Exception {
            String walletId = createWalletAndGetId("USD");

            // Deposit 100
            depositToWallet(walletId, new BigDecimal("100.00"), "USD");

            // Try to withdraw 150
            WithdrawRequest request = WithdrawRequest.builder()
                    .amount(new BigDecimal("150.00"))
                    .currency("USD")
                    .description("Too large withdrawal")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/withdraw", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Insufficient Balance"));
        }

        @Test
        @DisplayName("Should reject withdrawal from empty wallet")
        void shouldRejectWithdrawalFromEmptyWallet() throws Exception {
            String walletId = createWalletAndGetId("USD");

            WithdrawRequest request = WithdrawRequest.builder()
                    .amount(new BigDecimal("10.00"))
                    .currency("USD")
                    .description("Withdrawal from empty wallet")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/withdraw", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Insufficient Balance"));
        }

        @Test
        @DisplayName("Should allow withdrawal of exact balance")
        void shouldAllowWithdrawalOfExactBalance() throws Exception {
            String walletId = createWalletAndGetId("USD");

            // Deposit 100
            depositToWallet(walletId, new BigDecimal("100.00"), "USD");

            // Withdraw exactly 100
            WithdrawRequest request = WithdrawRequest.builder()
                    .amount(new BigDecimal("100.00"))
                    .currency("USD")
                    .description("Full withdrawal")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/withdraw", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.newBalance").value(0.00));
        }
    }

    @Nested
    @DisplayName("GET /api/wallets/{id}/transactions - Transaction History")
    class TransactionHistoryTests {

        @Test
        @DisplayName("Should return empty transaction history for new wallet")
        void shouldReturnEmptyHistoryForNewWallet() throws Exception {
            String walletId = createWalletAndGetId("USD");

            mockMvc.perform(get("/api/wallets/{walletId}/transactions", walletId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.walletId").value(walletId))
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.balance").value("0.00"))
                    .andExpect(jsonPath("$.totalEntries").value(0))
                    .andExpect(jsonPath("$.entries").isArray())
                    .andExpect(jsonPath("$.entries").isEmpty());
        }

        @Test
        @DisplayName("Should return transaction history with deposits and withdrawals")
        void shouldReturnHistoryWithMultipleTransactions() throws Exception {
            String walletId = createWalletAndGetId("USD");

            // Make some deposits
            depositToWallet(walletId, new BigDecimal("100.00"), "USD");
            depositToWallet(walletId, new BigDecimal("50.00"), "USD");

            // Make a withdrawal
            WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                    .amount(new BigDecimal("30.00"))
                    .currency("USD")
                    .description("Test withdrawal")
                    .build();

            mockMvc.perform(post("/api/wallets/{walletId}/withdraw", walletId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(withdrawRequest)))
                    .andExpect(status().isCreated());

            // Check transaction history
            mockMvc.perform(get("/api/wallets/{walletId}/transactions", walletId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.walletId").value(walletId))
                    .andExpect(jsonPath("$.currency").value("USD"))
                    .andExpect(jsonPath("$.balance").value("120.00"))
                    .andExpect(jsonPath("$.totalEntries").value(3))
                    .andExpect(jsonPath("$.entries").isArray())
                    .andExpect(jsonPath("$.entries", hasSize(3)))
                    // Most recent first (withdrawal should be first)
                    .andExpect(jsonPath("$.entries[0].type").value("DEBIT"))
                    .andExpect(jsonPath("$.entries[0].amount").value("30.00"));
        }

        @Test
        @DisplayName("Should return 404 for non-existent wallet transaction history")
        void shouldReturn404ForNonExistentWalletHistory() throws Exception {
            mockMvc.perform(get("/api/wallets/{walletId}/transactions", 
                    "00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    // Helper method to create a wallet and return its ID
    private String createWalletAndGetId(String currency) throws Exception {
        CreateWalletRequest request = CreateWalletRequest.builder()
                .currency(currency)
                .build();

        MvcResult result = mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }

    // Helper method to deposit money into a wallet
    private void depositToWallet(String walletId, BigDecimal amount, String currency) throws Exception {
        DepositRequest request = DepositRequest.builder()
                .amount(amount)
                .currency(currency)
                .description("Test deposit")
                .build();

        mockMvc.perform(post("/api/wallets/{walletId}/deposit", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
