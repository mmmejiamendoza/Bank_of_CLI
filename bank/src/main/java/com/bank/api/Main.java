package com.bank.api;
import com.bank.persistence.AccountDAO;
import com.bank.persistence.AccountDAOImpl;
import com.bank.persistence.ConnectionFactory;
import com.bank.service.BankService;
import com.bank.service.BankServiceImpl;

public class Main {
    public static void main(String[] args) {
        AccountDAO dao = new AccountDAOImpl(ConnectionFactory.getConnectionFactory());
        BankService service = new BankServiceImpl(dao);
        new BankRepl(service).run();
    }
}