package main.newsapp.models;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.utils.DatabaseHandler;
import main.newsapp.utils.DatabaseService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private String userName;
    private String password;
    private List<Article> readingHistory;
    private UserPreference preferences;
    private Map<Article, String> interactions;
//Thread pool for handling concurrent operation like login and registration
    public static final ExecutorService executorService = Executors.newCachedThreadPool();


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.readingHistory = new ArrayList<>();
        this.preferences = new UserPreference();//default preferences
        this.interactions = new HashMap<>();//this starts with an empty interaction map
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List<Article> getReadingHistory() {
        return readingHistory;
    }

    public UserPreference getPreferences() {
        return preferences;
    }

    public Map<Article, String> getInteractions() {
        return interactions;
    }
    // Method to set preferences directly
    public void setPreferences(UserPreference preferences) {
        this.preferences = preferences;
        logger.info("User preferences set directly.");
    }

    public void addToReadingHistory(Article article) {
        readingHistory.add(article);
        logger.info("Added to reading history: {}", article.getTitle());
    }

    public void updatePreferences(UserPreference preferences) {
        this.preferences = preferences;
        logger.info("User preferences updated.");
    }
    //Checks if the user exists in the database
    //The below method hides the internal logic of how database checks are performed bcz we are using dbservice here so
    //theres abstarction happening in this method
    public boolean login(DatabaseService dbService) throws UserNotFoundException, IncorrectPasswordException {
        boolean userExists = dbService.userExists(this.userName);
        if (!userExists) {
            throw new UserNotFoundException("User does not exist.");
        }

        boolean validPassword = dbService.validateUser(this.userName, this.password);
        if (!validPassword) {
            throw new IncorrectPasswordException("Incorrect password.");
        }

        logger.info("{} logged in successfully.", userName);
        return true;
    }

    //Ensures that duplicate users cannot register and performs the logging for the event
    public void register(DatabaseService dbService) throws DuplicateUserException {
        boolean userExists = dbService.userExists(this.userName);
        if (userExists) {
            throw new DuplicateUserException("User already exists.");
        }

        dbService.registerUser(this.userName, this.password);
        logger.info("{} has registered successfully.", userName);
    }

    public static void concurrentLogin(DatabaseService dbService, List<User> users) {
        users.forEach(user -> executorService.submit(() -> {
            try {
                user.login(dbService);
            } catch (Exception e) {
                logger.error("Error during login for user {}: {}", user.getUserName(), e.getMessage());
            }
        }));
    }

    public static void concurrentRegister(DatabaseService dbService, List<User> users) {
        users.forEach(user -> executorService.submit(() -> {
            try {
                user.register(dbService);
            } catch (Exception e) {
                logger.error("Error during registration for user {}: {}", user.getUserName(), e.getMessage());
            }
        }));
    }

    //this records when the user gives the rating and also synchronized bcz to
    //ensure thread safety
    public synchronized void recordInteraction(Article article, String feedback) {
        interactions.put(article, feedback);
        preferences.addCategory(article.getCategory());
        logger.info("Recorded interaction for article: {} | Feedback: {}", article.getTitle(), feedback);
    }

    public static void shutDownExecutor() {
        executorService.shutdown();
    }

    public void setPassword(String wrongPassword) {

    }


}