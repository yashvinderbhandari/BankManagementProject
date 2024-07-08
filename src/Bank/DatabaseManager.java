package Bank;

import java.sql.*;

public class DatabaseManager {
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bank";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "Yash@999";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

	}

	public static void createAccountsTable() {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {

			String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
					+ "account_number INT AUTO_INCREMENT PRIMARY KEY," + "account_holder VARCHAR(255) NOT NULL,"
					+ "balance DECIMAL(10,2) NOT NULL)";

			statement.execute(createTableSQL);

			System.out.println("Accounts table created successfully.");
			String createTransactionsTableSQL = "CREATE TABLE IF NOT EXISTS transactions ("
					+ "id INT AUTO_INCREMENT PRIMARY KEY," + "sender_account_number INT NOT NULL,"
					+ "receiver_account_number INT NOT NULL," + "amount DECIMAL(10,2) NOT NULL)";

			statement.execute(createTransactionsTableSQL);

			System.out.println("Accounts and Transactions tables created successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
