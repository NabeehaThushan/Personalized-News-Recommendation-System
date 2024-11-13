package newsapp.utils;

import newsapp.models.Category;
import newsapp.models.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler {
    private static final String DATABASE_URL = "jdbc:sqlite:user_data.db";

    public DatabaseHandler() {
        createTables();
    }

    private void createTables() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
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

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
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

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
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

}
