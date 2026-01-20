package com.fintech.wallet.infrastructure.persistence.mapper;

import com.fintech.wallet.domain.model.LedgerEntry;
import com.fintech.wallet.domain.model.LedgerEntryType;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.LedgerEntryId;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.TransactionId;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.infrastructure.persistence.entity.LedgerEntryJpaEntity;
import com.fintech.wallet.infrastructure.persistence.entity.LedgerEntryTypeJpa;
import com.fintech.wallet.infrastructure.persistence.entity.WalletJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between Wallet domain model and JPA entity.
 * Handles bidirectional conversion keeping domain pure.
 */
@Component
public class WalletMapper {

    public Wallet toDomain(WalletJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Currency currency = Currency.of(entity.getCurrency());

        List<LedgerEntry> ledgerEntries = entity.getLedgerEntries().stream()
                .map(this::toLedgerEntryDomain)
                .collect(Collectors.toList());

        return Wallet.reconstitute(
                WalletId.of(entity.getId()),
                currency,
                ledgerEntries,
                entity.getCreatedAt());
    }

    public WalletJpaEntity toJpaEntity(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        WalletJpaEntity entity = WalletJpaEntity.builder()
                .id(wallet.getId().getValue())
                .currency(wallet.getCurrency().getCode())
                .createdAt(wallet.getCreatedAt())
                .build();

        wallet.getLedgerEntries().forEach(ledgerEntry -> {
            LedgerEntryJpaEntity entryEntity = toLedgerEntryJpaEntity(ledgerEntry);
            entity.addLedgerEntry(entryEntity);
        });

        return entity;
    }

    public LedgerEntry toLedgerEntryDomain(LedgerEntryJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Currency currency = Currency.of(entity.getCurrency());
        Money amount = Money.of(entity.getAmount(), currency);

        return LedgerEntry.builder()
                .id(LedgerEntryId.of(entity.getId()))
                .walletId(WalletId.of(entity.getWallet().getId()))
                .transactionId(TransactionId.of(entity.getTransactionId()))
                .type(toDomainEntryType(entity.getEntryType()))
                .amount(amount)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public LedgerEntryJpaEntity toLedgerEntryJpaEntity(LedgerEntry entry) {
        if (entry == null) {
            return null;
        }

        return LedgerEntryJpaEntity.builder()
                .id(entry.getId().getValue())
                .transactionId(entry.getTransactionId().getValue())
                .entryType(toJpaEntryType(entry.getType()))
                .amount(entry.getAmount().getAmount())
                .currency(entry.getAmount().getCurrency().getCode())
                .description(entry.getDescription())
                .createdAt(entry.getCreatedAt())
                .build();
    }

    private LedgerEntryType toDomainEntryType(LedgerEntryTypeJpa jpaType) {
        return switch (jpaType) {
            case CREDIT -> LedgerEntryType.CREDIT;
            case DEBIT -> LedgerEntryType.DEBIT;
        };
    }

    private LedgerEntryTypeJpa toJpaEntryType(LedgerEntryType domainType) {
        return switch (domainType) {
            case CREDIT -> LedgerEntryTypeJpa.CREDIT;
            case DEBIT -> LedgerEntryTypeJpa.DEBIT;
        };
    }
}
