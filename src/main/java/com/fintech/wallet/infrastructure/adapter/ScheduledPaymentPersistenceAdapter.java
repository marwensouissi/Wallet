package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.ScheduledPaymentPort;
import com.fintech.wallet.domain.model.ScheduledPayment;
import com.fintech.wallet.domain.model.ScheduledPaymentStatus;
import com.fintech.wallet.domain.valueobject.ScheduledPaymentId;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.infrastructure.persistence.entity.ScheduledPaymentStatusJpa;
import com.fintech.wallet.infrastructure.persistence.mapper.ScheduledPaymentMapper;
import com.fintech.wallet.infrastructure.persistence.repository.ScheduledPaymentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Persistence adapter for scheduled payments.
 */
@Component
public class ScheduledPaymentPersistenceAdapter implements ScheduledPaymentPort {

    private final ScheduledPaymentJpaRepository repository;
    private final ScheduledPaymentMapper mapper;

    public ScheduledPaymentPersistenceAdapter(
            ScheduledPaymentJpaRepository repository,
            ScheduledPaymentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(ScheduledPayment payment) {
        repository.save(mapper.toEntity(payment));
    }

    @Override
    public Optional<ScheduledPayment> findById(ScheduledPaymentId id) {
        return repository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<ScheduledPayment> findBySourceWalletId(WalletId walletId) {
        return repository.findBySourceWalletId(walletId.getValue())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledPayment> findDuePayments(LocalDate date) {
        return repository.findDuePayments(date)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledPayment> findByStatus(ScheduledPaymentStatus status) {
        ScheduledPaymentStatusJpa statusJpa = ScheduledPaymentStatusJpa.valueOf(status.name());
        return repository.findByStatus(statusJpa)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduledPayment> findUpcomingPayments(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return repository.findUpcomingPayments(today, futureDate)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ScheduledPaymentId id) {
        repository.deleteById(id.getValue());
    }
}
