package com.bank.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.bank.domain.Account;
import com.bank.domain.Transaction;

public interface BankService {
    Account register(String pin);
    Optional<Account> login(int accountId, String pin);
    BigDecimal getBalance(int accountId);
    void deposit(int accountId, BigDecimal amount);
    void withdraw(int accountId, BigDecimal amount);
    void transfer(int fromAccountId, int toAccountId, BigDecimal amount);
    List<Transaction> getHistory(int accountId, int limit);
}
