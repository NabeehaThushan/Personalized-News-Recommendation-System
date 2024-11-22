package main.newsapp.utils;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {

    private HikariDataSource dataSource;
    private static final String DATABASE_URL = "jdbc:sqlite:user_data.db";

    public DatabaseHandler() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DATABASE_URL);
        config.setUsername("yourUsername");  // optional, if needed
        config.setPassword("yourPassword");  // optional, if needed
        config.setMaximumPoolSize(10); // Use a pool size suitable for your app
        dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)";
            String createPreferencesTable = "CREATE TABLE IF NOT EXISTS preferences (username TEXT, category TEXT, score INTEGER, FOREIGN KEY(username) REFERENCES users(username))";
            stmt.execute(createUserTable);
            stmt.execute(createPreferencesTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storePreferences(User user) {
        String insertUser = "INSERT OR REPLACE INTO users (username, password) VALUES (?, ?)";
        String insertPreference = "INSERT OR REPLACE INTO preferences (username, category, score) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(insertUser);
             PreparedStatement prefStmt = conn.prepareStatement(insertPreference)) {
            userStmt.setString(1, user.getUserName());
            userStmt.setString(2, user.getPassword());
            userStmt.executeUpdate();

            Map<Category, Integer> preferences = user.getPreferences().getAllPreferences();
            for (Map.Entry<Category, Integer> entry : preferences.entrySet()) {
                prefStmt.setString(1, user.getUserName());
                prefStmt.setString(2, entry.getKey().toString());
                prefStmt.setInt(3, entry.getValue());
                prefStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadPreferences(User user) {
        String selectPreferences = "SELECT category, score FROM preferences WHERE username = ?";
        Map<Category, Integer> preferences = new HashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectPreferences)) {
            stmt.setString(1, user.getUserName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = Category.valueOf(rs.getString("category"));
                int score = rs.getInt("score");
                preferences.put(category, score);
            }
            user.getPreferences().setAllPreferences(preferences);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String username) {
        String query = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateUser(String username, String password) throws UserNotFoundException, IncorrectPasswordException {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (!rs.getString("password").equals(password)) {
                    throw new IncorrectPasswordException("Incorrect password for username: " + username);
                }
                return true;
            } else {
                throw new UserNotFoundException("User not found: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registerUser(String username, String password) throws DuplicateUserException {
        if (userExists(username)) {
            throw new DuplicateUserException("User already exists: " + username);
        }
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
