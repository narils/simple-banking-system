package banking.account;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.Random;

public class Account {
    private static final Random random = new Random();
    private static final DecimalFormat pinFormater = new DecimalFormat("###0");
    private static final String accountPattern = "^400000\\d{9,12}$";
    private final String cardNumber, pin;
    private final int id;
    private final int balance;

    int getId() {
        return id;
    }

    Account(int id, String cardNumber, String pin, int balance) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    static String generatePin() {
        int pin = random.nextInt(9000) + 1000;
        return pinFormater.format(pin);
    }

    static String getCardNumber(String accountNumber) {
        if (!accountNumber.matches(accountPattern)) {
            System.out.println("Wrong account number provided");
            return null;
        }
        int sum = 0;
        char[] chars = accountNumber.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int val = Integer.parseInt(String.valueOf(chars[i]));

            if (i % 2 == 0) {
                val *= 2;
            }
            if (val > 9) {
                val -= 9;
            }
            sum += val;
        }
        return accountNumber + (10 - (sum % 10)) % 10;
    }

    static boolean verifyCardNumber(String accountNumber) {
        String checkedNumber = getCardNumber(accountNumber.substring(0, accountNumber.length() - 1));
        return accountNumber.equals(checkedNumber);
    }

    String getCardNumber() {
        return cardNumber;
    }

    int getBalance() {
        return balance;
    }

    boolean verifyPin(String pin) {
        return this.pin.equals(pin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return cardNumber.equals(account.cardNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber);
    }
}
