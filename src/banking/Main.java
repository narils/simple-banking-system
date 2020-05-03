package banking;

import banking.account.AccountConnection;
import banking.account.AccountMenu;
import banking.menu.MainMenu;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        String dbFilePath = null;

        for (int i = 0; i < args.length; i++) {
            String currentArg = args[i];

            if (currentArg.equals("-fileName") && args.length - 1 > i) {
                dbFilePath = args[++i];
            }
        }

        if (dbFilePath == null) {
            System.out.println("Missing input file -fileName parameter");
            return;
        }

        // Start program
        MainState state = MainState.MAIN;
        AccountConnection db = new AccountConnection(dbFilePath);
        Scanner scanner = new Scanner(System.in);
        MainMenu mainMenu = new MainMenu(scanner);
        AccountMenu accountMenu = new AccountMenu(scanner, db);

        try {
            while (state != null) {
                switch (state) {
                    case MAIN:
                        state = mainMenu.start();
                        break;
                    case ACCOUNT:
                        state = accountMenu.start();
                        break;
                    case CREATE_ACCOUNT:
                        accountMenu.createAccount();
                        state = MainState.MAIN;
                        break;
                    default:
                        state = null;
                        break;
                }
            }
        } finally {
            db.close();
        }
    }
}
