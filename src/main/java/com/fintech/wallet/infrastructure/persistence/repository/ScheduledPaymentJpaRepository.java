package com.fintech.wallet.infrastructure.persistence.repository;

import com.fintech.wallet.infrastructure.persistence.entity.ScheduledPaymentJpaEntity;
import com.fintech.wallet.infrastructure.persistence.entity.ScheduledPaymentStatusJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * JPA repository for scheduled payments.
 */
@Repository
public interface ScheduledPaymentJpaRepository extends JpaRepository<ScheduledPaymentJpaEntity, UUID> {

    List<ScheduledPaymentJpaEntity> findBySourceWalletId(UUID sourceWalletId);

    List<ScheduledPaymentJpaEntity> findByStatus(ScheduledPaymentStatusJpa status);

    @Query("SELECT p FROM ScheduledPaymentJpaEntity p WHERE p.status = 'ACTIVE' AND p.nextExecutionDate <= :date")
    List<ScheduledPaymentJpaEntity> findDuePayments(@Param("date") LocalDate date);

    @Query("SELECT p FROM ScheduledPaymentJpaEntity p WHERE p.status = 'ACTIVE' AND p.nextExecutionDate <= :futureDate AND p.nextExecutionDate > :today")
    List<ScheduledPaymentJpaEntity> findUpcomingPayments(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);
}
