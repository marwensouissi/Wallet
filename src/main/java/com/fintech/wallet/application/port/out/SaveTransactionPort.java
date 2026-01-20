package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.model.Transaction;

/**
 * Output port for saving transactions to persistence.
 * Implemented by infrastructure adapters.
 */
public interface SaveTransactionPort {

    /**
     * Saves a transaction record.
     *
     * @param transaction the transaction to save
     */
    void save(Transaction transaction);
}
