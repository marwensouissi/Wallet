package com.fintech.wallet.infrastructure.persistence.repository;

import com.fintech.wallet.infrastructure.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for transaction persistence.
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {

    /**
     * Finds all transactions where the wallet is the source.
     */
    List<TransactionJpaEntity> findBySourceWalletIdOrderByCreatedAtDesc(UUID walletId);

    /**
     * Finds all transactions where the wallet is the destination.
     */
    List<TransactionJpaEntity> findByDestinationWalletIdOrderByCreatedAtDesc(UUID walletId);
}
