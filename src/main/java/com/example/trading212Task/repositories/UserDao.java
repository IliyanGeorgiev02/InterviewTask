package com.example.trading212Task.repositories;

import com.example.trading212Task.dtos.UserRegisterDTO;
import com.example.trading212Task.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;

@Repository
public class UserDao {
        @Autowired
        private DataSource dataSource;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public boolean userExists(String username, String email) {
            String sql = "SELECT * FROM users WHERE username = ? OR email = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);
                stmt.setString(2, email);

                ResultSet rs = stmt.executeQuery();

                return rs.next();

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

    public boolean saveUser(User user) {
        String userSql = "INSERT INTO users (username, firstName, lastName, email, password) VALUES (?, ?, ?, ?, ?)";
        String balanceSql = "INSERT INTO account_balances (user_id, balance) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement balanceStmt = conn.prepareStatement(balanceSql)) {

            userStmt.setString(1, user.getUsername());
            userStmt.setString(2, user.getFirstName());
            userStmt.setString(3, user.getLastName());
            userStmt.setString(4, user.getEmail());
            userStmt.setString(5, passwordEncoder.encode(user.getPassword()));

            int userRows = userStmt.executeUpdate();

            if (userRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);

                    balanceStmt.setLong(1, userId);
                    balanceStmt.setDouble(2, 10000.00);

                    int balanceRows = balanceStmt.executeUpdate();

                    return balanceRows > 0;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
