package com.fintech.wallet.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for withdrawal operations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawResponse {

    private String ledgerEntryId;
    private String walletId;
    private BigDecimal amount;
    private String currency;
    private BigDecimal newBalance;
    private String description;
    private Instant timestamp;
}
