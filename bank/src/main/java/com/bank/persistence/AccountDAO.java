package com.bank.persistence;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bank.domain.Account;
import com.bank.domain.Transaction;

public interface AccountDAO {
    Account createAccount(String pin);
    Optional<Account> findById(int accountId);
    void deposit(int accountId, BigDecimal amount);
    void withdraw(int accountId, BigDecimal amount);
    void transfer(int fromAccountId, int toAccountId, BigDecimal amount);
    List<Transaction> findRecentTransactions(int accountId, int limit);
    
    class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
}
