package com.bank.persistence;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bank.domain.Account;
import com.bank.domain.Transaction;

public class AccountDAOImpl implements AccountDAO {
    private static final Logger LOGGER = Logger.getLogger(AccountDAOImpl.class.getName());
    private static final String OVERDRAW_SQLSTATE = "23514";
    private final ConnectionFactory connectionFactory;

    public AccountDAOImpl(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Account createAccount(String pin) {
        String sql = "INSERT INTO account (pin, balance) VALUES (?, 0.00) RETURNING account_id, balance";
        try(Connection conn = connectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pin);
            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                LOGGER.info("CReated account " + rs.getInt("account_id"));
                return new Account(rs.getInt("account_id"), pin, rs.getBigDecimal("balance"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "failed to create amount", e);
            throw new IllegalStateException("couldn't create accoutn", e);
        }
    }

    @Override
    public Optional<Account> findById(int accountId) {
        String sql = "SELECT account_id, pin, balance FROM account WHERE account_id = ?";
        try (Connection conn = connectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try(ResultSet rs = ps.executeQuery()) {
                if(!rs.next()) return Optional.empty(); 
                return Optional.of(new Account(rs.getInt("account_id"), rs.getString("pin"), rs.getBigDecimal("balance")));
            }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "failed to look up account " + accountId, e);
        throw new IllegalStateException("couldnt look up acccount", e);
        }
    } 

    @Override 
    public void deposit(int accountId, BigDecimal amount) {
        try(Connection conn = connectionFactory.getConnection()){
            conn.setAutoCommit(false);
            try{
                updateBalance(conn, accountId, amount);
                insertTransaction(conn, accountId, Transaction.Type.DEPOSIT, amount, null);
                conn.commit();
                LOGGER.info("Depoisted " + amount + " into account " + accountId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "deposit failed for this account" + accountId, e);
            throw new IllegalStateException("couldnt process deposit", e);
        }
    }

    @Override 
    public void withdraw(int accountId, BigDecimal amount) {
        try(Connection conn = connectionFactory.getConnection()){
            conn.setAutoCommit(false);
            try {
                updateBalance(conn, accountId, amount.negate());
                insertTransaction(conn, accountId, Transaction.Type.WITHDRAW, amount, null);
                conn.commit();
                LOGGER.info("withdraw " + amount + " from account " + accountId);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "withdraw failed for this account" + accountId, e);
            throw new IllegalStateException("couldnt process withrdrawl", e);
        }
    }

    @Override 
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        try(Connection conn = connectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                updateBalance(conn, fromAccountId, amount.negate());
                updateBalance(conn, toAccountId, amount);
                insertTransaction(conn, fromAccountId, Transaction.Type.TRANSFER_OUT, amount, toAccountId);
                insertTransaction(conn, toAccountId, Transaction.Type.TRANSFER_IN, amount, fromAccountId);
                conn.commit();
                LOGGER.info("transfeered " + amount + " from " + fromAccountId + " to " + toAccountId);
            } catch (SQLException e){
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if(OVERDRAW_SQLSTATE.equals(e.getSQLState())) {
                LOGGER.warning("rejected overdraw transfer from account " + fromAccountId);
                throw new InsufficientFundsException("insufiicent funds for transfer");
            }
            LOGGER.log(Level.SEVERE, "transfer failed from " + fromAccountId + " to " + toAccountId, e);
            throw new IllegalStateException("couldnt process transfer", e);
        }
    }

    @Override
    public List<Transaction> findRecentTransactions(int accountId, int limit){
        String sql = "SELECT transaction_id, account_id, type, amount, related_account_id, timestamp" + "FROM transactions WHERE account_id = ? ORDER BY timestamp DESC LIMIT ?";
        List<Transaction> results = new ArrayList<>();
        try(Connection conn = connectionFactory.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Integer related = rs.getObject("related_account_id") != null ? rs.getInt("related_account_id") : null;
                    results.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("account_id"),
                        Transaction.Type.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        related,
                        rs.getTimestamp("timestamp").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "failed to fetch hiostory for ammount " + accountId, e);
            throw new IllegalStateException("couldnt fetch transaction history", e);
        }
        return results;
    }

    private void updateBalance(Connection conn, int accountId, BigDecimal delta) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, delta);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    private void insertTransaction(Connection conn, int accountId, Transaction.Type type, BigDecimal amount, Integer relatedAccountId) throws SQLException {
        String sql = "INSERT INTO transaction (account_id, amount, related_account_id) VALUES (?, ?, ?, ?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type.name());
            ps.setBigDecimal(3, amount);
            if(relatedAccountId != null) ps.setInt(4, relatedAccountId);
            else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        }
    }
}
