package com.fintech.wallet.infrastructure.persistence.mapper;

import com.fintech.wallet.domain.model.RecurrencePattern;
import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.model.ScheduledPaymentStatus;
import com.fintech.wallet.domain.valueobject.Currency;
import com.fintech.wallet.domain.valueobject.Money;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.infrastructure.persistence.entity.RecurrencePatternJpa;
import com.fintech.wallet.infrastructure.persistence.entity.ScheduledPaymentJpaEntity;
import com.fintech.wallet.infrastructure.persistence.entity.ScheduledPaymentStatusJpa;
import org.springframework.stereotype.Component;

/**
 * Mapper between ScheduledPayment domain model and JPA entity.
 */
@Component
public class ScheduledPaymentMapper {

    public ScheduledPaymentJpaEntity toEntity(ScheduledPayment payment) {
        return ScheduledPaymentJpaEntity.builder()
                .id(payment.getId().getValue())
                .sourceWalletId(payment.getSourceWalletId().getValue())
                .destinationWalletId(payment.getDestinationWalletId().getValue())
                .amount(payment.getAmount().getAmount())
                .currency(payment.getAmount().getCurrency().getCode())
                .description(payment.getDescription())
                .recurrencePattern(RecurrencePatternJpa.valueOf(payment.getRecurrencePattern().name()))
                .startDate(payment.getStartDate())
                .endDate(payment.getEndDate())
                .nextExecutionDate(payment.getNextExecutionDate())
                .executionCount(payment.getExecutionCount())
                .maxExecutions(payment.getMaxExecutions())
                .status(ScheduledPaymentStatusJpa.valueOf(payment.getStatus().name()))
                .createdAt(payment.getCreatedAt())
                .lastModifiedAt(payment.getLastModifiedAt())
                .build();
    }

    public ScheduledPayment toDomain(ScheduledPaymentJpaEntity entity) {
        return ScheduledPayment.builder()
                .id(ScheduledPaymentId.of(entity.getId()))
                .sourceWalletId(WalletId.of(entity.getSourceWalletId()))
                .destinationWalletId(WalletId.of(entity.getDestinationWalletId()))
                .amount(Money.of(entity.getAmount(), Currency.of(entity.getCurrency())))
                .description(entity.getDescription())
                .recurrencePattern(RecurrencePattern.valueOf(entity.getRecurrencePattern().name()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .nextExecutionDate(entity.getNextExecutionDate())
                .executionCount(entity.getExecutionCount())
                .maxExecutions(entity.getMaxExecutions())
                .status(ScheduledPaymentStatus.valueOf(entity.getStatus().name()))
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build();
    }
}
