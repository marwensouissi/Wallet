package com.fintech.wallet.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for transfer transaction data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {

    private String transactionId;
    private String sourceWalletId;
    private String destinationWalletId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private Instant timestamp;
}
