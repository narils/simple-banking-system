package banking.menu;

import banking.MainState;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


enum MainOptions {
    CREATE_ACCOUNT("Create account"),
    LOG_IN("Log into account");

    private final String action;

    MainOptions(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}

public class MainMenu extends Menu {
    private final List<MainOptions> options;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
        options = new ArrayList<>();

        options.add(MainOptions.CREATE_ACCOUNT);
        options.add(MainOptions.LOG_IN);
    }

    @Override
    public MainState start() {
        MainOptions selection = getInput(options);
        if (selection == null) {
            return null;
        }

        switch (selection) {
            case CREATE_ACCOUNT:
                return MainState.CREATE_ACCOUNT;
            case LOG_IN:
                return MainState.ACCOUNT;
            default:
                return null;
        }

    }
}
