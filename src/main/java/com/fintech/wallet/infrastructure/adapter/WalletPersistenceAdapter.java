package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.LoadWalletPort;
import com.fintech.wallet.application.port.out.SaveWalletPort;
import com.fintech.wallet.domain.model.Wallet;
import com.fintech.wallet.domain.valueobject.WalletId;
import com.fintech.wallet.infrastructure.persistence.entity.WalletJpaEntity;
import com.fintech.wallet.infrastructure.persistence.mapper.WalletMapper;
import com.fintech.wallet.infrastructure.persistence.repository.WalletJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence adapter implementing wallet-related output ports.
 * Bridges domain and infrastructure layers.
 */
@Component
public class WalletPersistenceAdapter implements LoadWalletPort, SaveWalletPort {

    private final WalletJpaRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletPersistenceAdapter(WalletJpaRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public Optional<Wallet> loadById(WalletId walletId) {
        return walletRepository.findByIdWithLedgerEntries(walletId.getValue())
                .map(walletMapper::toDomain);
    }

    @Override
    public void save(Wallet wallet) {
        Optional<WalletJpaEntity> existingEntity = walletRepository.findById(wallet.getId().getValue());

        if (existingEntity.isPresent()) {
            WalletJpaEntity entity = existingEntity.get();

            wallet.getLedgerEntries().forEach(ledgerEntry -> {
                boolean exists = entity.getLedgerEntries().stream()
                        .anyMatch(e -> e.getId().equals(ledgerEntry.getId().getValue()));

                if (!exists) {
                    entity.addLedgerEntry(walletMapper.toLedgerEntryJpaEntity(ledgerEntry));
                }
            });

            walletRepository.save(entity);
        } else {
            WalletJpaEntity newEntity = walletMapper.toJpaEntity(wallet);
            walletRepository.save(newEntity);
        }
    }
}
