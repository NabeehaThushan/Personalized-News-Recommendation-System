package java.models;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String userName;
    private String password;
    private List<Article> readingHistory;
    private UserPreference preferences;
    private Map<Article, String> interactions;
    private static final String USER_DATA_FILE = "users.txt";

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

    public boolean login(){
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData[0].equals(this.userName) && userData[1].equals(this.password)) {
                    System.out.println(userName + " logged in successfully.");
                    return true;
                }
            }

        } catch (IOException e) {
            System.out.println("Error during login: "+ e.getMessage());
        }
        System.out.println("Invalid Username or Password");
        return false;
    }

    public void register(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE,true))) {
            writer.write(this.userName + "," + this.password + "\n");
            System.out.println(userName + "has registered successfully.");

        } catch (IOException e) {
            System.out.println("Error during registeration: "+ e.getMessage());
        }
    }

    public static List<User> loadUsers(){
        List<User> users = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] userData = line.split(",");
                if (userData.length == 2){
                    users.add(new User(userData[0],userData[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error during loading users: "+ e.getMessage());
        }
        return users;
    }

}
