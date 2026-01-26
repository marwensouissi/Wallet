package com.fintech.wallet.interfaces.rest.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * Response DTO for a ledger entry in transaction history.
 */
@Value
@Builder
public class LedgerEntryResponse {
    String id;
    String walletId;
    String transactionId;
    String type;
    String amount;
    String currency;
    String description;
    Instant createdAt;
}
