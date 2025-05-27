package com.example.trading212Task.repositories;

import com.example.trading212Task.config.UserSession;
import com.example.trading212Task.pojos.Transaction;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionsDao {

    private final UserSession userSession;
    private final DataSource dataSource;


    public TransactionsDao(UserSession userSession, DataSource dataSource) {
        this.userSession = userSession;
        this.dataSource = dataSource;
    }

    public void insertBuyTransaction(String cryptoSymbol, String type, double amount, double price, double totalPriceUSD) {
        String sql = "INSERT INTO transactions (user_id, crypto_symbol, type, amount, price, timestamp,totalPrice) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, this.userSession.getUserId());
            stmt.setString(2, cryptoSymbol);
            stmt.setString(3, type);
            stmt.setDouble(4, amount);
            stmt.setDouble(5, price);
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setDouble(7, totalPriceUSD);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSellTransaction(String cryptoSymbol, String type, double amount, double price, double totalPriceUSD) {
        String sql = "INSERT INTO transactions (user_id, crypto_symbol, type, amount, price, timestamp, totalPrice) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, this.userSession.getUserId());
            stmt.setString(2, cryptoSymbol);
            stmt.setString(3, type);
            stmt.setDouble(4, amount);
            stmt.setDouble(5, price);
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setDouble(7, totalPriceUSD);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getAllTransactionsByUserId(long userId) {
        String sql = "SELECT crypto_symbol, type, amount, price, timestamp, totalPrice " +
                "FROM transactions WHERE user_id=? ORDER BY timestamp DESC";
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setSymbol(rs.getString("crypto_symbol"));
                transaction.setType(rs.getString("type"));

                transaction.setAmount(rs.getInt("amount"));

                transaction.setPrice(rs.getDouble("price"));

                Timestamp timestamp = rs.getTimestamp("timestamp");
                if (timestamp != null) {
                    transaction.setDateTime(timestamp.toLocalDateTime());
                } else {
                    transaction.setDateTime(LocalDateTime.now());
                }
                transaction.setTotalPrice(rs.getDouble("totalPrice"));

                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching transactions for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    public void deleteAllTransactionsForUser(Long userId) {
        String sql = "DELETE FROM transactions WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
