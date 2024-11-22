package main.newsapp.models;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.utils.DatabaseHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class User {
    private String userName;
    private String password;
    private List<Article> readingHistory;
    private UserPreference preferences;
    private Map<Article, String> interactions;

    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.readingHistory = new ArrayList<>();
        this.preferences = new UserPreference();
        this.interactions = new HashMap<>();

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Article> getReadingHistory() {
        return readingHistory;
    }

    public void setReadingHistory(List<Article> readingHistory) {
        this.readingHistory = readingHistory;
    }

    public UserPreference getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreference preferences) {
        this.preferences = preferences;
    }

    public Map<Article, String> getInteractions() {
        return interactions;
    }

    public void setInteractions(Map<Article, String> interactions) {
        this.interactions = interactions;
    }

    public void addToReadingHistory(Article article){
        readingHistory.add(article);
        System.out.println("Added to reading history." + article.getTitle());
    }

    public void updatePreferences(UserPreference preferences){
        this.preferences = preferences;
        System.out.println("User preferences updated.");
    }

    public boolean login(DatabaseHandler dbHandler) throws UserNotFoundException, IncorrectPasswordException{
       // Handle login in a concurrent environment, no synchronized block
        boolean userExists = dbHandler.userExists(this.userName);
        if (!userExists) {
            throw new UserNotFoundException("User does not exist.");
        }

        boolean validPassword = dbHandler.validateUser(this.userName, this.password);
        if (!validPassword) {
            throw new IncorrectPasswordException("Incorrect password.");
        }

        System.out.println(userName + " logged in successfully.");
        return true;
    }


    public void register(DatabaseHandler dbHandler) throws DuplicateUserException {
        // Register the user without synchronized block, managed by the database handler
        boolean userExists = dbHandler.userExists(this.userName);
        if (userExists) {
            throw new DuplicateUserException("User already exists.");
        }

        dbHandler.registerUser(this.userName, this.password);
        System.out.println(userName + " has registered successfully.");
    }


    // Concurrent login task
    public static void concurrentLogin(DatabaseHandler dbHandler, List<User> users) {
        users.forEach(user -> executorService.submit(() -> {
            try {
                user.login(dbHandler);
            } catch (Exception e) {
                System.out.println("Error during login for user: " + user.getUserName() + " - " + e.getMessage());
            }
        }));
    }

    // Concurrent register task
    public static void concurrentRegister(DatabaseHandler dbHandler, List<User> users) {
        users.forEach(user -> executorService.submit(() -> {
            try {
                user.register(dbHandler);
            } catch (Exception e) {
                System.out.println("Error during registration for user: " + user.getUserName() + " - " + e.getMessage());
            }
        }));
    }

    // Record user interaction with an article
    public void recordInteraction(Article article, String feedback) {
        interactions.put(article, feedback);
        System.out.println("Recorded interaction for article: " + article.getTitle() + " | Feedback: " + feedback);
    }

    public static void shutDownExecutor() {
        executorService.shutdown();
    }
}



