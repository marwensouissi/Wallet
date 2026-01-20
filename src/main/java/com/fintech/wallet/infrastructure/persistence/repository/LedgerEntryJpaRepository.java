package com.fintech.wallet.infrastructure.persistence.repository;

import com.fintech.wallet.infrastructure.persistence.entity.LedgerEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for ledger entry persistence.
 */
@Repository
public interface LedgerEntryJpaRepository extends JpaRepository<LedgerEntryJpaEntity, UUID> {

    /**
     * Finds all ledger entries for a specific wallet.
     */
    List<LedgerEntryJpaEntity> findByWalletIdOrderByCreatedAtAsc(UUID walletId);

    /**
     * Finds all ledger entries for a specific transaction.
     */
    List<LedgerEntryJpaEntity> findByTransactionId(UUID transactionId);
}
