package banking.menu;

import banking.MainState;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class Menu {
    protected Scanner scanner;

    protected abstract MainState start() throws SQLException;

    protected <E extends Enum<E>> E getInput(List<E> options) {
        Integer selection = -1;

        while (selection == null || selection > options.size() || selection < 0) {
            int menuChoice = 1;
            for (E option : options) {
                System.out.println(menuChoice++ + ". " + option);
            }
            System.out.println("0. Exit");
            selection = getNumberFromInput();
        }
        System.out.println();
        if (selection == 0) {
            return null;
        }
        return options.get(selection - 1);
    }

    /**
     * Try to parse a number from input
     * Returns null if none is found.
     */
    protected Integer getNumberFromInput() {
        Integer selection = null;
        try {
            selection = scanner.nextInt();
        } catch (NoSuchElementException e) {
            System.out.println("Invalid input");
        }
        scanner.nextLine();
        return selection;
    }

    /**
     * Return a numberstring or null if none is found
     */
    protected String getNumberFromInputAsString() {
        String selection = null;
        try {
            selection = scanner.next("\\d+");
        } catch (NoSuchElementException e) {
            System.out.println("Invalid input");
        }
        scanner.nextLine();
        return selection;
    }
}
