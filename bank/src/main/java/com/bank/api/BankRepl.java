package com.bank.api;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.persistence.AccountDAO;
import com.bank.service.BankService;

public class BankRepl {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankRepl.class.getName());
    private final BankService service;
    private final Scanner scanner = new Scanner(System.in);
    private Account currentAccount;

    public BankRepl(BankService service) {
        this.service = service;
    }

    public void run() {
        System.out.println("-------- BANK OF CLI --------");
        while (currentAccount == null) {
            System.out.println("1) register     2) login    3) exit");
            switch(scanner.nextLine().trim()){
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    return;
                }
                default -> System.out.println("choose from one of the following");
            }
        }
        menu();
    }

    private void register() {
        System.out.print("choose a PIN: ");
        String pin = scanner.nextLine();
        Account acct = service.register(pin);
        System.out.println("account has been made! your account ID is: " + acct.getAccountID());
        currentAccount = acct;
    }

    private void login() {
        System.out.print("account ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("PIN: ");
        String pin = scanner.nextLine();
        Optional<Account> acct = service.login(id, pin);
        if(acct.isEmpty()) {
            System.out.println("incorrect account ID/PIN");
        } else {
            currentAccount = acct.get();
            System.out.println("welcome back!");
        }
    }

    private void menu() {
        boolean running = true;
        while(running) {
            System.out.println("\n1) balance    2) deposit      3) withdraw     4) transfer     5) history      6) exit");
            try {
                switch(scanner.nextLine().trim()) {
                    case "1" -> System.out.println("balance: $" + service.getBalance(currentAccount.getAccountID()));
                    case "2" -> deposit();
                    case "3" -> withdraw();
                    case "4" -> transfer();
                    case "5" -> history();
                    case "6" -> running = false;
                    default -> System.out.println("choose from one of the following");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("error: " + e.getMessage());
            } catch (AccountDAO.InsufficientFundsException e) {
                System.out.println("sorry, insufficent funds");
            } catch (Exception e) {
                LOGGER.error("unexpceted error", e);
                System.out.println("service is unavaiable, try again");
            }
        }
    }

    private void deposit() {
        System.out.print("amount to deposit: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        service.deposit(currentAccount.getAccountID(), amount);
        System.out.println("deposit succesfull!");
    }

    private void withdraw() {
        System.out.print("amount to withdraw: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        service.withdraw(currentAccount.getAccountID(), amount);
        System.out.println("withdraw succesfull!");
    }

    private void transfer() {
        System.out.print("other person's account ID: ");
        int toId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("amount to tranfer: $");
        BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
        service.transfer(currentAccount.getAccountID(), toId, amount);
        System.out.println("transfer successfull!");
    }

    private void history() {
        List<Transaction> trans = service.getHistory(currentAccount.getAccountID(), 10);
        if(trans.isEmpty()) {
            System.out.println("no transaction yet");
        } else {
            trans.forEach(System.out::println);
        }
    }
}
