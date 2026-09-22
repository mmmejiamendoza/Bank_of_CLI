package com.bank.api;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bank.persistence.AccountDAO;
import com.bank.persistence.AccountDAOImpl;
import com.bank.persistence.ConnectionFactory;
import com.bank.service.BankService;
import com.bank.service.BankServiceImpl;

public class Main {
    public static void main(String[] args) throws IOException {
        FileHandler fileHandler = new FileHandler("bank.log", true);
        fileHandler.setFormatter(new SimpleFormatter());
        Logger.getLogger("").addHandler(fileHandler);

        AccountDAO dao = new AccountDAOImpl(ConnectionFactory.getConnectionFactory());
        BankService service = new BankServiceImpl(dao);
        new BankRepl(service).run();
    }
}