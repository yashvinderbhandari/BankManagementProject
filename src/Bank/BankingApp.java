package Bank;

import java.sql.*;
import java.util.Scanner;

public class BankingApp {
    public static void main(String[] args) {
    	DatabaseManager.createAccountsTable();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create Account");
            System.out.println("2. Transfer Funds");
            System.out.println("3. View Transaction History");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter account holder name: ");
                    String accountHolder = scanner.nextLine();
                    System.out.print("Enter initial balance: ");
                    double initialBalance = scanner.nextDouble();

                    createAccount(accountHolder, initialBalance);
                    break;

                case 2:
                    System.out.print("Enter sender account number: ");
                    int senderAccountNumber = scanner.nextInt();
                    System.out.print("Enter receiver account number: ");
                    int receiverAccountNumber = scanner.nextInt();
                    System.out.print("Enter amount to transfer: ");
                    double transferAmount = scanner.nextDouble();

                    transferFunds(senderAccountNumber, receiverAccountNumber, transferAmount);
                    break;

                case 3:
                    System.out.print("Enter account number: ");
                    int accountNumber = scanner.nextInt();
                    viewTransactionHistory(accountNumber);
                    break;

                case 4:
                    System.out.println("Exiting the application. Goodbye!");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    public static void createAccount(String accountHolder, double initialBalance) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO accounts (account_holder, balance) VALUES (?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, accountHolder);
            preparedStatement.setDouble(2, initialBalance);
            System.out.println("In create account\n");
            preparedStatement.executeUpdate();
          
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int accountNumber = generatedKeys.getInt(1);
                System.out.println("Account created successfully. Account Number: " + accountNumber);
            } else {
                System.out.println("Failed to retrieve account number.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  
    public static void transferFunds(int senderAccountNumber, int receiverAccountNumber, double amount) {
        try (Connection connection = DatabaseManager.getConnection()) {
            // Check sender and receiver accounts
            Account senderAccount = getAccount(connection, senderAccountNumber);
            Account receiverAccount = getAccount(connection, receiverAccountNumber);

            if (senderAccount == null || receiverAccount == null) {
                System.out.println("Invalid account numbers. Please check and try again.");
                return;
            }

            // Check if the sender has enough balance
            if (senderAccount.getBalance() < amount) {
                System.out.println("Insufficient funds. Transfer cannot be completed.");
                return;
            }

            // Update balances
            updateBalance(connection, senderAccountNumber, senderAccount.getBalance() - amount);
            updateBalance(connection, receiverAccountNumber, receiverAccount.getBalance() + amount);

            // Record transaction history
            recordTransaction(connection, senderAccountNumber, receiverAccountNumber, amount);

            System.out.println("Funds transferred successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewTransactionHistory(int accountNumber) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM transactions WHERE sender_account_number = ? OR receiver_account_number = ?")) {

            preparedStatement.setInt(1, accountNumber);
            preparedStatement.setInt(2, accountNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Transaction History:");
            System.out.println("Sender   | Receiver | Amount");
            System.out.println("-----------------------------");

            while (resultSet.next()) {
                int sender = resultSet.getInt("sender_account_number");
                int receiver = resultSet.getInt("receiver_account_number");
                double amount = resultSet.getDouble("amount");

                System.out.println(sender + "      | " + receiver + "      | " + amount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private static Account getAccount(Connection connection, int accountNumber) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM accounts WHERE account_number = ?")) {

            preparedStatement.setInt(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Account account = new Account();
                account.setAccountNumber(resultSet.getInt("account_number"));
                account.setAccountHolder(resultSet.getString("account_holder"));
                account.setBalance(resultSet.getDouble("balance"));
                return account;
            } else {
                return null;
            }
        }
    }

    private static void updateBalance(Connection connection, int accountNumber, double newBalance) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE accounts SET balance = ? WHERE account_number = ?")) {

            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setInt(2, accountNumber);

            preparedStatement.executeUpdate();
        }
    }

    private static void recordTransaction(Connection connection, int senderAccount, int receiverAccount, double amount)
            throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO transactions (sender_account_number, receiver_account_number, amount) VALUES (?, ?, ?)")) {

            preparedStatement.setInt(1, senderAccount);
            preparedStatement.setInt(2, receiverAccount);
            preparedStatement.setDouble(3, amount);

            preparedStatement.executeUpdate();
        }
      
    }
  
   
}



