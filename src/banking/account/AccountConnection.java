package banking.account;

import java.sql.*;

public class AccountConnection {
    private final String url;
    private final Connection conn;

    public AccountConnection(String fileName) {
        url = "jdbc:sqlite:" + fileName;
        conn = connect();
        createTable();
    }

    public void close() throws SQLException {
        this.conn.close();
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS card (\n" +
                "id INTEGER,\n" +
                "number TEXT,\n" +
                "pin TEXT,\n" +
                "balance INTEGER DEFAULT 0\n" +
                ");";
        boolean result = executeQuery(query);
    }

    private boolean executeQuery(String query) {
        try {
            Statement stmt = conn.createStatement();
            return stmt.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    String getNextAccountNumber() throws SQLException {
        String query = "SELECT CAST(SUBSTR(MAX(number), 0, 16) AS integer) + 1 as next from card;";
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(query);

        try {
            result.next();
            String accountNumber = result.getString("next");
            return accountNumber == null ? "400000100000000" : accountNumber;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    int getNextId() throws SQLException {
        String query = "SELECT IFNULL(MAX(id),0) + 1 as next from card;";
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(query);
        result.next();
        return result.getInt("next");
    }

    Account getAccountFromAccount(Account account) throws SQLException {
        return getAccountFromCardNumber(account.getCardNumber());
    }

    Account getAccountFromCardNumber(String cardNumber) throws SQLException {
        String query = "SELECT id, pin, balance\n" +
                "FROM card\n" +
                "WHERE number = ?;";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, cardNumber);

        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            String pin = rs.getString("pin");
            int balance = rs.getInt("balance");
            return new Account(id, cardNumber, pin, balance);
        }

        return null;
    }

    /**
     * Delete account. May silently fail if DB is not reachable.
     */
    void deleteAccount(Account account) throws SQLException {
        String query = "DELETE FROM CARD WHERE id = ?;";
        PreparedStatement pstm = conn.prepareStatement(query);
        pstm.setInt(1, account.getId());

        int result = pstm.executeUpdate();
    }

    void updateBalance(Account account, int amount) throws SQLException {
        String query = "UPDATE CARD SET balance = balance + ? where id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, amount);
        pstmt.setInt(2, account.getId());

        int result = pstmt.executeUpdate();
    }


    Account createAccount(String cardNumber, String pin) throws SQLException {
        int nextId = getNextId();
        String query = "INSERT INTO card(id, number, pin)\n" +
                "VALUES(?, ?, ?);";

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, nextId);
        pstmt.setString(2, cardNumber);
        pstmt.setString(3, pin);

        boolean result = pstmt.execute();
        if (!result) {
            return getAccountFromCardNumber(cardNumber);
        }
        return null;
    }
}
