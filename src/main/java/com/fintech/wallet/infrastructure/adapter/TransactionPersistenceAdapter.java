package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.SaveTransactionPort;
import com.fintech.wallet.domain.model.Transaction;
import com.fintech.wallet.infrastructure.persistence.entity.TransactionJpaEntity;
import com.fintech.wallet.infrastructure.persistence.mapper.TransactionMapper;
import com.fintech.wallet.infrastructure.persistence.repository.TransactionJpaRepository;
import org.springframework.stereotype.Component;

/**
 * Persistence adapter implementing transaction-related output ports.
 * Bridges domain and infrastructure layers.
 */
@Component
public class TransactionPersistenceAdapter implements SaveTransactionPort {

    private final TransactionJpaRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionPersistenceAdapter(TransactionJpaRepository transactionRepository,
            TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public void save(Transaction transaction) {
        TransactionJpaEntity entity = transactionMapper.toJpaEntity(transaction);
        transactionRepository.save(entity);
    }
}
