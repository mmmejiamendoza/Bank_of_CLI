package com.bank.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.persistence.AccountDAO;

public class BankServiceImpl implements BankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankServiceImpl.class);
    private final AccountDAO accountDAO;

    public BankServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override 
    public Account register(String pin){
        return accountDAO.createAccount(pin);
    }

    @Override 
    public Optional<Account> login(int accountId, String pin) {
        Optional<Account> account = accountDAO.findById(accountId);
        if (account.isEmpty() || !account.get().getPin().equals(pin)) {
            LOGGER.warn("failed login attempt for thy account " + accountId);
            return Optional.empty();
        }
        LOGGER.info("account " + accountId + " logged in succesffuly");
        return account;
    }

    @Override 
    public BigDecimal getBalance(int accountId) {
        return accountDAO.findById(accountId).orElseThrow(() -> new IllegalArgumentException("no such account")).getBalance();
    }


    @Override
    public void deposit(int accountId, BigDecimal amount) {
        requirePositive(amount);
        accountDAO.deposit(accountId, amount);
    }

    @Override
    public void withdraw(int accountId, BigDecimal amount) {
        requirePositive(amount);
        accountDAO.withdraw(accountId, amount);
    }

    @Override
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        requirePositive(amount);
        if(fromAccountId == toAccountId) {
            throw new IllegalArgumentException("cant transfer to the same account");
        }
        accountDAO.transfer(fromAccountId, toAccountId, amount);
    }

    @Override 
    public List<Transaction> getHistory(int account_id, int limit) {
        return accountDAO.findRecentTransactions(account_id, limit);
    }
    private void requirePositive(BigDecimal amount) {
        if(amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be more than zero");
        }
    }
}
