package banking.account;

import banking.MainState;
import banking.menu.Menu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum AccountOptions {
    BALANCE("Balance"),
    ADD_INCOME("Add income"),
    TRANSFER("Do transfer"),
    CLOSE("Close account"),
    LOG_OUT("Log out");

    private final String action;

    AccountOptions(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}

public class AccountMenu extends Menu {
    private final List<AccountOptions> options;
    private Account account;
    private final AccountConnection db;

    public AccountMenu(Scanner scanner, AccountConnection db) {
        this.scanner = scanner;
        this.db = db;

        options = new ArrayList<>();
        options.add(AccountOptions.BALANCE);
        options.add(AccountOptions.ADD_INCOME);
        options.add(AccountOptions.TRANSFER);
        options.add(AccountOptions.CLOSE);
        options.add(AccountOptions.LOG_OUT);
    }

    private Account login() throws SQLException {
        account = null;
        String cardNumber = null;
        String pin = null;

        while (cardNumber == null) {
            System.out.println("Enter your card number:");
            cardNumber = getNumberFromInputAsString();
        }

        while (pin == null) {
            System.out.println("Enter your PIN:");
            pin = getNumberFromInputAsString();
        }

        System.out.println();
        Account account = db.getAccountFromCardNumber(cardNumber);
        if (account == null || !account.verifyPin(pin)) {
            return null;
        }
        return account;

    }

    public void createAccount() throws SQLException {
        Account account;
        String accountNumber = db.getNextAccountNumber();
        String cardNumber = Account.getCardNumber(accountNumber);
        String pin = Account.generatePin();

        if (cardNumber != null) {
            account = db.createAccount(cardNumber, pin);
        } else {
            System.out.println("Error generating number and pin");
            return;
        }
        if (account == null) {
            System.out.println("Error creating new account");
            return;
        }
        System.out.println(
                "Your card has been created\n" +
                        "Your card number\n" +
                        cardNumber + "\n" +
                        "Your card pin:\n" +
                        pin);
        System.out.println();
    }

    private Account addIncome() throws SQLException {
        System.out.println("How much would you like to deposit?");
        Integer amount = getNumberFromInput();
        System.out.println();

        db.updateBalance(account, amount);
        return db.getAccountFromAccount(account);

    }

    private void transfer() throws SQLException {
        System.out.println("Which card would you like to transfer to?");
        String transferToCardNumber = getNumberFromInputAsString();
        Account transferTo = db.getAccountFromCardNumber(transferToCardNumber);

        if (account.getCardNumber().equals(transferToCardNumber)) {
            System.out.println("You can't transfer money to the same account!");
        } else if (!Account.verifyCardNumber(transferToCardNumber)) {
            System.out.println("Probably you made mistake in card number. Please try again!");
        } else if (transferTo == null) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("How much would you like to transfer?");
            Integer amount = getNumberFromInput();
            System.out.println();
            if (amount != null) {
                db.updateBalance(transferTo, amount);
                db.updateBalance(account, -amount);

                // refresh changes from DB
                account = db.getAccountFromAccount(account);
            }
        }
        System.out.println();
    }

    @Override
    public MainState start() throws SQLException {
        this.account = login();
        if (account == null) {
            System.out.println("Wrong card number or PIN!");
            System.out.println();
            return MainState.MAIN;
        }

        System.out.println("You have successfully logged in!");
        System.out.println();

        while (true) {
            AccountOptions selection = getInput(options);
            if (selection == null) {
                return null;
            }

            switch (selection) {
                case BALANCE:
                    System.out.println("Balance: " + account.getBalance());
                    break;
                case ADD_INCOME:
                    account = addIncome();
                    break;
                case TRANSFER:
                    transfer();
                    break;
                case CLOSE:
                    db.deleteAccount(account);
                    // CONTINUES IN LOGOUT CASE
                case LOG_OUT:
                    this.account = null;
                    return MainState.MAIN;
                default:
                    return null;
            }
            System.out.println();
        }
    }
}
