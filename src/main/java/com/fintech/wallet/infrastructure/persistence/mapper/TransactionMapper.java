package com.fintech.wallet.infrastructure.persistence.mapper;

import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.domain.model.TransactionStatus;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.infrastructure.persistence.entity.TransactionJpaEntity;
import com.fintech.wallet.infrastructure.persistence.entity.TransactionStatusJpa;
import org.springframework.stereotype.Component;

/**
 * Mapper between Transaction domain model and JPA entity.
 * Handles bidirectional conversion keeping domain pure.
 */
@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Currency currency = Currency.of(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);

        return Transaction.builder()
                .id(TransactionId.of(entity.getId()))
                .sourceWalletId(WalletId.of(entity.getSourceWalletId()))
                .destinationWalletId(WalletId.of(entity.getDestinationWalletId()))
                .amount(amount)
                .description(entity.getDescription())
                .status(toDomainStatus(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public TransactionJpaEntity toJpaEntity(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionJpaEntity.builder()
                .id(transaction.getId().getValue())
                .sourceWalletId(transaction.getSourceWalletId().getValue())
                .destinationWalletId(transaction.getDestinationWalletId().getValue())
                .amount(transaction.getAmount().getAmount())
                .currency(transaction.getAmount().getCurrency().getCode())
                .description(transaction.getDescription())
                .status(toJpaStatus(transaction.getStatus()))
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private TransactionStatus toDomainStatus(TransactionStatusJpa jpaStatus) {
        return switch (jpaStatus) {
            case PENDING -> TransactionStatus.PENDING;
            case COMPLETED -> TransactionStatus.COMPLETED;
            case FAILED -> TransactionStatus.FAILED;
            case REVERSED -> TransactionStatus.REVERSED;
        };
    }

    private TransactionStatusJpa toJpaStatus(TransactionStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> TransactionStatusJpa.PENDING;
            case COMPLETED -> TransactionStatusJpa.COMPLETED;
            case FAILED -> TransactionStatusJpa.FAILED;
            case REVERSED -> TransactionStatusJpa.REVERSED;
        };
    }
}
