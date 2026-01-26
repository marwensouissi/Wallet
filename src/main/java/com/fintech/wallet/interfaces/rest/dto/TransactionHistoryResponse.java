package com.fintech.wallet.interfaces.rest.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Response DTO for wallet transaction history.
 */
@Value
@Builder
public class TransactionHistoryResponse {
    String walletId;
    String currency;
    String balance;
    int totalEntries;
    List<LedgerEntryResponse> entries;
}
