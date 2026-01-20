package com.fintech.wallet.infrastructure.persistence.repository;

import com.fintech.wallet.infrastructure.persistence.entity.WalletJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for wallet persistence.
 */
@Repository
public interface WalletJpaRepository extends JpaRepository<WalletJpaEntity, UUID> {

    /**
     * Loads a wallet with all its ledger entries eagerly.
     */
    @Query("SELECT w FROM WalletJpaEntity w LEFT JOIN FETCH w.ledgerEntries WHERE w.id = :id")
    Optional<WalletJpaEntity> findByIdWithLedgerEntries(@Param("id") UUID id);
}
