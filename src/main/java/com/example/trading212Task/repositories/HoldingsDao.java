package com.example.trading212Task.repositories;

import com.example.trading212Task.pojos.Holding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HoldingsDao {
    @Autowired
    private DataSource dataSource;

    public double getUserHoldingAmount(Long userId, String symbol) {
        String sql = "SELECT amount FROM holdings WHERE user_id = ? AND crypto_symbol = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, symbol);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public boolean deleteUserHolding(Long userId, String symbol) {
        String sql = "DELETE FROM holdings WHERE user_id = ? AND crypto_symbol = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, symbol);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Holding> getUserHoldings(Long userId) {
        String sql = "SELECT crypto_symbol, amount FROM holdings WHERE user_id = ?";
        List<Holding> holdings = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Holding holding = new Holding();
                holding.setSymbol(rs.getString("crypto_symbol"));
                holding.setAmount(rs.getDouble("amount"));
                holdings.add(holding);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return holdings;
    }

    public boolean holdingExists(Long userId, String symbol) {
        String sql = "SELECT 1 FROM holdings WHERE user_id = ? AND crypto_symbol = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, symbol);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void increaseHolding(Long userId, String symbol, double quantity) {
        String sql = "UPDATE holdings SET amount = amount + ? WHERE user_id = ? AND crypto_symbol = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, quantity);
            stmt.setLong(2, userId);
            stmt.setString(3, symbol);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void insertHolding(Long userId, String symbol, double quantity) {
        String sql = "INSERT INTO holdings (user_id, crypto_symbol, amount) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, symbol);
            stmt.setDouble(3, quantity);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllHoldingsForUser(Long userId) {
        String sql = "DELETE FROM holdings WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decreaseHolding(Long userId, String symbol, double quantity) {
        String sql = "UPDATE holdings SET amount = amount - ? WHERE user_id = ? AND crypto_symbol = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, quantity);
            stmt.setLong(2, userId);
            stmt.setString(3, symbol);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
