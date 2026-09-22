package com.bank.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN
    }

    private final int transactionId;
    private final int accountId;
    private final Type type;
    private final BigDecimal amount;
    private final Integer relatedAccountId; //this would be null unless its a transfer
    private final LocalDateTime timestamp;

    public Transaction(int transactionId, int accountId, Type type, BigDecimal amount, Integer relatedAccountId, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.relatedAccountId = relatedAccountId;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }
    public int getAccountID() {
        return accountId;
    }
    public Type getType() {
        return type;
    }
    public BigDecimal getBigDecimal() {
        return amount;
    }
    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s%s", timestamp, type, amount, 
        relatedAccountId != null ? " (acct " + relatedAccountId + ")" : "");
    }
    
}
