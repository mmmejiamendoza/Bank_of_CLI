package com.bank.domain;
import java.math.BigDecimal;

public class Account {
    private final int accountId;
    private final String pin;
    private BigDecimal balance;

    public Account(int accountID, String pin, BigDecimal balance) {
        this.accountId = accountID;
        this.pin = pin;
        this.balance = balance;
    }

    public int getAccountID() {
        return accountId;
    }
    public String getPin() {
        return pin;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
