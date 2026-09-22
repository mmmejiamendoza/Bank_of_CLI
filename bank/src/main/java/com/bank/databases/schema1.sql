-- account and transaction tables

CREATE TABLE account (
    account_id SERIAL PRIMARY KEY,
    pin VARCHAR(255) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT balance_non_negative CHECK (balance >= 0)
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    account_id INTEGER NOT NULL REFERENCES account(account_id),
    type VARCHAR(20) NOT NULL,
    amount NUMERIC (12, 3) NOT NULL,
    related_amount_id INTEGER REFERENCES account(account_id),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transaction_amount_time
    ON transaction (account_id, timestamp DESC);

