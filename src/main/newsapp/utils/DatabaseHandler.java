package main.newsapp.utils;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHandler implements DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private HikariDataSource dataSource;
    private static final String DATABASE_URL = "jdbc:sqlite:C:\\Users\\nabee\\IdeaProjects\\PersonalizedNewsRecommendationSystem\\user_data.db";


    public DatabaseHandler() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DATABASE_URL);
        config.setMaximumPoolSize(10); // Use a pool size suitable for your app
        dataSource = new HikariDataSource(config);
        createTables();
    }

    private void createTables() {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {

        // Debug logging to confirm method execution
        logger.info("Attempting to create tables...");

        String createUserTable = "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)";
        String createPreferencesTable = "CREATE TABLE IF NOT EXISTS preferences (username TEXT, category TEXT, score INTEGER, FOREIGN KEY(username) REFERENCES users(username))";
        String createArticleRatingsTable = "CREATE TABLE IF NOT EXISTS article_ratings (" +
                "username TEXT, " +
                "article_id TEXT, " +
                "rating INTEGER, " +
                "FOREIGN KEY(username) REFERENCES users(username), " +
                "PRIMARY KEY(username, article_id))";


        stmt.execute(createUserTable);
        stmt.execute(createPreferencesTable);
        stmt.execute(createArticleRatingsTable);


        logger.info("Tables created successfully.");
    } catch (SQLException e) {
        logger.error("Error creating tables: {}", e.getMessage());
        e.printStackTrace();
    }
}

    public void resetUserPreferences(String username) {
    String query = "DELETE FROM preferences WHERE username = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.executeUpdate();
        System.out.println("Preferences reset for user: " + username);
    } catch (SQLException e) {
        System.out.println("Error resetting preferences for user " + username + ": " + e.getMessage());
    }
}

    public void clearUserReadingHistory(String username) {
    String query = "DELETE FROM reading_history WHERE username = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.executeUpdate();
        System.out.println("Reading history cleared for user: " + username);
    } catch (SQLException e) {
        System.out.println("Error clearing reading history for user " + username + ": " + e.getMessage());
    }
}



public void logInteraction(String username, String articleId, int rating) {
    String query = "INSERT INTO interaction_history (username, article_id, rating, timestamp) VALUES (?, ?, ?, ?)";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, username);
        pstmt.setString(2, articleId);
        pstmt.setInt(3, rating);
        pstmt.setString(4, new java.util.Date().toString());
        pstmt.executeUpdate();
        logger.info("Logged interaction: {} | {} | {}", username, articleId, rating);
    } catch (SQLException e) {
        logger.error("Error logging interaction: {}", e.getMessage());
    }
}


    @Override
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
            logger.error("Error storing preferences for user {}: {}", user.getUserName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
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
            logger.error("Error loading preferences for user {}: {}", user.getUserName(), e.getMessage());
            e.printStackTrace();
        }
    }

        @Override
    public void storeArticleRating(String username, String articleId, int rating) {
        String query = "INSERT INTO article_ratings (username, article_id, rating) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, articleId);
            stmt.setInt(3, rating);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error storing article rating for user {}: {}", username, e.getMessage());
            e.printStackTrace();
        }
    }


    public Map<String, Integer> getUserRatings(String username) {
        String query = "SELECT article_id, rating FROM article_ratings WHERE username = ?";
        Map<String, Integer> ratings = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ratings.put(rs.getString("article_id"), rs.getInt("rating"));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving ratings for user {}: {}", username, e.getMessage());
            e.printStackTrace();
        }
        return ratings;
    }

    @Override
    public boolean userExists(String username) {
        String query = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.error("Error checking if user exists: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
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
            logger.error("Error validating user {}: {}", username, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
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
            logger.error("Error registering user {}: {}", username, e.getMessage());
            e.printStackTrace();
        }
    }


    public void storeReadingHistory(User user) {
    String checkQuery = "SELECT COUNT(*) FROM reading_history WHERE username = ? AND article_id = ?";
    String insertQuery = "INSERT INTO reading_history (username, article_id, article_name) VALUES (?, ?, ?)";

    try (Connection conn = dataSource.getConnection()) {
        for (Article article : user.getReadingHistory()) {
            // Check if the article is already in the reading history
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, user.getUserName());
                checkStmt.setString(2, article.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Article already exists in history; skip adding
                    continue;
                }
            }

            // Add article to reading history if not already present
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, user.getUserName());
                insertStmt.setString(2, article.getId());
                insertStmt.setString(3, article.getTitle());
                insertStmt.executeUpdate();
            }
        }
    } catch (SQLException e) {
        logger.error("Error storing reading history for user {}: {}", user.getUserName(), e.getMessage());
    }
}


public void loadReadingHistory(User user, List<Article> allArticles) {
    String query = "SELECT article_id FROM reading_history WHERE username = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, user.getUserName());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String articleId = rs.getString("article_id");
            allArticles.stream()
                    .filter(article -> article.getId().equals(articleId))
                    .findFirst()
                    .ifPresent(user::addToReadingHistory);
        }
    } catch (SQLException e) {
        logger.error("Error loading reading history for user {}: {}", user.getUserName(), e.getMessage());
    }
}




}


