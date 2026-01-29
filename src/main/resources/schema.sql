-- Database schema for FinTech Wallet Application
-- PostgreSQL compatible

-- Wallets table
CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT chk_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_wallets_created_at ON wallets(created_at);

-- Ledger entries table
CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL,
    transaction_id UUID NOT NULL,
    entry_type VARCHAR(10) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_ledger_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT chk_entry_type CHECK (entry_type IN ('CREDIT', 'DEBIT')),
    CONSTRAINT chk_amount_positive CHECK (amount >= 0),
    CONSTRAINT chk_currency_format CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_ledger_wallet_id ON ledger_entries(wallet_id);
CREATE INDEX idx_ledger_transaction_id ON ledger_entries(transaction_id);
CREATE INDEX idx_ledger_created_at ON ledger_entries(created_at);

-- Transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    source_wallet_id UUID NOT NULL,
    destination_wallet_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_transaction_source FOREIGN KEY (source_wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_transaction_destination FOREIGN KEY (destination_wallet_id) REFERENCES wallets(id),
    CONSTRAINT chk_different_wallets CHECK (source_wallet_id <> destination_wallet_id),
    CONSTRAINT chk_transaction_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_transaction_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REVERSED')),
    CONSTRAINT chk_transaction_currency CHECK (currency ~ '^[A-Z]{3}$')
);

CREATE INDEX idx_transactions_source ON transactions(source_wallet_id);
CREATE INDEX idx_transactions_destination ON transactions(destination_wallet_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_status ON transactions(status);

-- Comments for documentation
COMMENT ON TABLE wallets IS 'Wallet aggregate root - holds currency and references to ledger entries';
COMMENT ON TABLE ledger_entries IS 'Immutable ledger entries - balance is calculated from these, not stored';
COMMENT ON TABLE transactions IS 'Transaction records for money transfers between wallets';

COMMENT ON COLUMN ledger_entries.entry_type IS 'CREDIT increases balance, DEBIT decreases balance';
COMMENT ON COLUMN ledger_entries.amount IS 'Always positive - sign determined by entry_type';
COMMENT ON COLUMN transactions.status IS 'Transaction status: PENDING, COMPLETED, FAILED, or REVERSED';

-- Scheduled payments table
CREATE TABLE scheduled_payments (
    id UUID PRIMARY KEY,
    source_wallet_id UUID NOT NULL,
    destination_wallet_id UUID NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    description VARCHAR(255),
    recurrence_pattern VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    next_execution_date DATE,
    execution_count INTEGER NOT NULL DEFAULT 0,
    max_executions INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_modified_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_scheduled_source FOREIGN KEY (source_wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_scheduled_destination FOREIGN KEY (destination_wallet_id) REFERENCES wallets(id),
    CONSTRAINT chk_scheduled_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_scheduled_currency CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT chk_scheduled_recurrence CHECK (recurrence_pattern IN ('ONCE', 'DAILY', 'WEEKLY', 'BIWEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY')),
    CONSTRAINT chk_scheduled_status CHECK (status IN ('ACTIVE', 'PAUSED', 'COMPLETED', 'CANCELLED', 'FAILED'))
);

CREATE INDEX idx_scheduled_source ON scheduled_payments(source_wallet_id);
CREATE INDEX idx_scheduled_destination ON scheduled_payments(destination_wallet_id);
CREATE INDEX idx_scheduled_next_execution ON scheduled_payments(next_execution_date);
CREATE INDEX idx_scheduled_status ON scheduled_payments(status);

COMMENT ON TABLE scheduled_payments IS 'Scheduled and recurring payment configurations';
COMMENT ON COLUMN scheduled_payments.recurrence_pattern IS 'ONCE for one-time, DAILY, WEEKLY, BIWEEKLY, MONTHLY, QUARTERLY, or YEARLY';
COMMENT ON COLUMN scheduled_payments.max_executions IS '0 for unlimited recurring payments';

-- ShedLock table for distributed locking
CREATE TABLE shedlock (
    name VARCHAR(64) PRIMARY KEY,
    lock_until TIMESTAMP NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    locked_by VARCHAR(255) NOT NULL
);

COMMENT ON TABLE shedlock IS 'ShedLock table for distributed scheduler locking across multiple instances';
