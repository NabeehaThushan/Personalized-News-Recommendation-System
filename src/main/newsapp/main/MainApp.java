package main.newsapp.main;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import main.newsapp.models.*;
import main.newsapp.services.RecommendationEngine;
import main.newsapp.utils.FileHandler;
import main.newsapp.utils.DatabaseHandler;
import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.newsapp.utils.DatabaseService;
public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static DatabaseService dbService;
    private static RecommendationEngine recommendationEngine;
    private static User currentUser;
    private static List<Article> availableArticles;
    private static Admin admin;

    public static void main(String[] args) {
        dbService = new DatabaseHandler();
        availableArticles = new FileHandler().fetchArticlesFromFile();
        recommendationEngine = new RecommendationEngine(availableArticles, dbService); // Set ML model to false initially
        admin = new Admin("NabeehaThushan", "admin");

        System.out.println("Welcome to the Personalized News Recommendation System!");
        simulateConcurrentUsers();
        while (true) {
            showLoginRegisterMenu();
        }
    }

    // Simulate multiple concurrent users
    //This shows that multiple users can log in at the same time and the program does different task at the same time
    private static void simulateConcurrentUsers() {
    int numberOfSimulatedUsers = 5; // Simulate 5 concurrent users
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfSimulatedUsers);

    // Pre-register users to avoid "User does not exist" errors
    for (int i = 1; i <= numberOfSimulatedUsers; i++) {
        String username = "User" + i;
        String password = "password" + i;
        try {
            if (!dbService.userExists(username)) {
                User user = new User(username, password);
                user.register(dbService);
                System.out.println("Pre-registered " + username);
            }
        } catch (Exception e) {
            System.err.println("Error registering user " + username + ": " + e.getMessage());
        }
    }

    // Simulate user actions concurrently
    for (int i = 1; i <= numberOfSimulatedUsers; i++) {
        int userId = i;
        executorService.submit(() -> {
            try {
                simulateUserActions("User" + userId, "password" + userId);
            } catch (Exception e) {
                System.err.println("Error during user simulation for User" + userId + ": " + e.getMessage());
            }
        });
    }

    executorService.shutdown();
    try {
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    } catch (InterruptedException e) {
        executorService.shutdownNow();
        Thread.currentThread().interrupt();
    }
}


    // Simulate user actions for a given username and password
    public static void simulateUserActions(String username, String password) {
    System.out.println(Thread.currentThread().getName() + ": " + username + " is attempting to log in...");

    User user = new User(username, password);
    try {
        if (user.login(dbService)) {
            System.out.println(Thread.currentThread().getName() + ": " + username + " logged in successfully.");

            // Load user preferences and reading history
            dbService.loadPreferences(user);
            dbService.loadReadingHistory(user, availableArticles);

            // Perform actions
            performUserActions(user);

            System.out.println(Thread.currentThread().getName() + ": " + username + " logged out.");
        } else {
            System.out.println(Thread.currentThread().getName() + ": Login failed for " + username);
        }
    } catch (UserNotFoundException | IncorrectPasswordException e) {
        System.err.println(Thread.currentThread().getName() + ": Error for " + username + ": " + e.getMessage());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
    }


    // Perform various user actions (view, recommend, update preferences)
    public static void performUserActions(User user) throws InterruptedException {
        // Simulate viewing articles
        System.out.println(user.getUserName() + " is viewing articles...");
        Thread.sleep(1000); // Simulate time delay

        // Simulate getting recommendations
        System.out.println(user.getUserName() + " is getting recommendations...");
        List<Article> recommendations = recommendationEngine.generateRecommendations(user);
        for (Article article : recommendations) {
            System.out.println("Recommended for " + user.getUserName() + ": " + article.getTitle() + " - " + article.getCategory());
        }

        Thread.sleep(1000); // Simulate time delay

        // Simulate updating preferences
        System.out.println(user.getUserName() + " is updating preferences...");
        user.getPreferences().setPreference(Category.SCIENCE, 5); // Example preference update
        dbService.storePreferences(user);
        System.out.println(user.getUserName() + "'s preferences updated.");
    }

    public static void showLoginRegisterMenu() {
    System.out.println("\nChoose an option:");
    System.out.println("\n1. Register");
    System.out.println("2. Login");
    System.out.println("3. Admin Login");
    System.out.println("4. Exit");

    if (currentUser != null) {
        // User is already logged in
        System.out.println("You are already logged in as: " + currentUser.getUserName());
        // Option to log out or proceed
        System.out.println("5. Log Out");
        int choice = getIntInput("\nEnter your choice: ");
        switch (choice) {
            case 1:
                System.out.println("You are already logged in. No need to register again.");
                showArticleMenu();
                break;
            case 2:
                System.out.println("You are already logged in. Proceeding to the menu...");
                showArticleMenu();
                break;
            case 3:
                System.out.println("Admin Login is not allowed while logged in as a user.");
                break;
            case 4:
                logOut();
                break;
            case 5:
                System.out.println("Exiting program... Thanks for using our system!!>o<.");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again:(");
        }
    } else {
        int choice = getIntInput("\nEnter your choice: ");
        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                adminLogin();
                break;
            case 4:
                System.out.println("Exiting program...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
}



    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    public static void register() {
    System.out.print("Enter username: ");
    String username = scanner.nextLine();

    String password;
    while (true) {
        System.out.print("Enter password: ");
        password = scanner.nextLine();
        if (password.trim().isEmpty()) {
            System.out.println("Password cannot be empty. Please enter a valid password.");
        } else {
            break;
        }
    }

    try {
        currentUser = new User(username, password);
        currentUser.register(dbService);
        System.out.println("Registration successful. Redirecting to the main menu....");
        showArticleMenu();
    } catch (DuplicateUserException e) {
        System.out.println("Error during registration: " + e.getMessage()) ;
    }
}

    public static void login() {
    if (currentUser != null) {
        System.out.println("You are already logged in.'0'");
        return;
    }

    System.out.print("Enter username: ");
    String username = scanner.nextLine();
    System.out.print("Enter password: ");
    String password = scanner.nextLine();

    User tempUser = new User(username, password);

    try {
        if (tempUser.login(dbService)) {
            currentUser = tempUser; // Set the current user only after successful login

            // Load preferences from the database
            dbService.loadPreferences(currentUser);
            System.out.println("Loaded Preferences: " + currentUser.getPreferences().getAllPreferences());

            // Load reading history from the database
            dbService.loadReadingHistory(currentUser, availableArticles);
            System.out.println("Loaded Reading History: ");
            for (Article article : currentUser.getReadingHistory()) {
                System.out.println(" - " + article.getTitle() + " | " + article.getCategory());
            }

            System.out.println("Login successful.");
            showArticleMenu(); // Proceed to the article menu
        }
    } catch (UserNotFoundException | IncorrectPasswordException e) {
        System.out.println("Error during login: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Unexpected error during login: " + e.getMessage());
    }
}

// Admin login
    public static void adminLogin() {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        if (!username.equals("NabeehaThushan")) { // Validate username first
        System.out.println("Invalid admin username.");
        return;
    }
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (admin.getUserName().equals(username) && admin.getPassword().equals(password)) {
            System.out.println("Admin login successful.");
            showAdminMenu();
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    public static void showAdminMenu() {
        while (true) {
            System.out.println("\n~~Admin Menu~~:");
            System.out.println("\n1. Add Article");
            System.out.println("2. Remove Article");
            System.out.println("3. List All Articles");
            System.out.println("4. View User Activity");
            System.out.println("5. Reset User Preferences");
            System.out.println("6. Log Out");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    addArticle();
                    break;
                case 2:
                    removeArticle();
                    break;
                case 3:
                    admin.listAllArticles(availableArticles);
                    break;
                case 4:
                    viewUserActivity();
                    break;
                case 5:
                    resetUserPreferences();
                    break;
                case 6:
                    System.out.println("Logging out of admin view...Byeee");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void addArticle() {
    System.out.print("Enter article title: ");
    String title = scanner.nextLine();
    System.out.print("Enter article description: ");
    String description = scanner.nextLine();
    System.out.print("Enter article content: ");
    String content = scanner.nextLine();
    System.out.print("Enter article source: ");
    String source = scanner.nextLine();
    System.out.print("Enter article published date (YYYY-MM-DD): ");
    String publishedDateInput = scanner.nextLine();
    System.out.print("Enter article category: ");
    String categoryInput = scanner.nextLine();

    try {
        // Validate category
        Category articleCategory = Category.valueOf(categoryInput.toUpperCase().replace(" ", "_"));

        // Parse published date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date publishedDate = dateFormat.parse(publishedDateInput);

        // Create the new Article object
        Article newArticle = new Article(title, description, content, articleCategory, source, publishedDate);

        // Add the article using the admin's method
        admin.addArticle(availableArticles, newArticle);
        FileHandler.backupArticlesFile();
        FileHandler.saveArticlesToFile(availableArticles); // Persist the article to the JSON file
        System.out.println("Article added successfully!");
    } catch (IllegalArgumentException e) {
        System.out.println("Invalid category. Please try again.");
    } catch (ParseException e) {
        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
    }
}


    // Admin functionality: Remove article
    public static void removeArticle() {
        System.out.print("Enter article ID to remove: ");
        String articleId = scanner.nextLine();
        admin.removeArticle(availableArticles, articleId);
    }

    // Admin functionality: View user activity
    public static void viewUserActivity() {
        System.out.print("Enter username to view activity: ");
        String username = scanner.nextLine();

        try {
            User user = new User(username, ""); // Temporary user object
            dbService.loadReadingHistory(user, availableArticles);
            dbService.loadPreferences(user);
            admin.viewUserActivity(user);
        } catch (Exception e) {
            System.out.println("Error viewing user activity: " + e.getMessage());
        }
    }

    // Admin functionality: Reset user preferences
    public static void resetUserPreferences() {
    System.out.print("Enter the username of the user whose preferences you want to reset: ");
    String username = scanner.nextLine();

    User targetUser = getUserByUsername(username); // Validate user existence
    if (targetUser != null) {
        admin.resetUserPreferences(targetUser);
        dbService.storePreferences(targetUser); // Save reset preferences to the database
        System.out.println("Preferences reset for user: " + username);
    } else {
        System.out.println("User not found: " + username);
    }
}

    private static User getUserByUsername(String username) {
        // Add a method to fetch the user by username
        if (dbService.userExists(username)) {
            // Returning a temporary user object (actual preferences/history loaded elsewhere)
            return new User(username, "");
        }
        return null;
    }

    public static void clearUserReadingHistory() {
        System.out.print("Enter the username of the user whose reading history you want to clear: ");
        String username = scanner.nextLine();
        admin.clearUserReadingHistory(dbService, username);
    }


    public static void showArticleMenu() {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. View Articles");
            System.out.println("2. Get Recommendations");
            System.out.println("3. Update Preferences");
            System.out.println("4. Log Out");
            System.out.println("5. Exit");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1:
                    viewArticles();
                    break;
                case 2:
                    getRecommendations();
                    break;
                case 3:
                    updatePreferences();
                    break;
                case 4:
                    logOut();
                    return;
                case 5:
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

   public static void viewArticles() {
    if (availableArticles.isEmpty()) {
        printColoredText("No articles available.", "\033[31m"); // Red color for no articles message
        return;
    }

    printColoredText("Available Articles:", "\033[34m"); // Blue color for header
    for (int i = 0; i < availableArticles.size(); i++) {
        System.out.println((i + 1) + ". " + availableArticles.get(i).getTitle());
    }

    printColoredText("Enter the number to view an article, or 0 to go back:", "\033[33m"); // Yellow color for prompt
    int choice = getIntInput("Your choice: ");

    if (choice > 0 && choice <= availableArticles.size()) {
        Article selectedArticle = availableArticles.get(choice - 1);

        // Display full details with color formatting
        printColoredText("\nFull Article Details:", "\033[32m"); // Green color for section title
        printColoredText("Title: " + selectedArticle.getTitle(), "\033[34m"); // Blue color for title
        printColoredText("| Source: " + selectedArticle.getSource() + " || " + "Published Date: " + selectedArticle.getPublishedDate(), "\033[36m"); // Cyan color for source and date

        printColoredText("\n--Overview: " + selectedArticle.getDescription() + "--", "\033[35m"); // Magenta color for overview
        printColoredText("\nContent: ", "\033[33m"); // Yellow color for content section header

        // Format and print the content in paragraphs
        printFormattedContent(selectedArticle.getContentOfArticle());

        printColoredText("(Published Date: " + selectedArticle.getPublishedDate() + ")", "\033[37m"); // White color for published date

        // Add the article to the user's reading history
        currentUser.addToReadingHistory(selectedArticle);
        dbService.storeReadingHistory(currentUser);

        // Ask for rating with color prompt
        printColoredText("\nWould you like to rate this article? (Enter a rating from 1-5, or 0 to skip)", "\033[32m"); // Green color for prompt
        int rating = getIntInput("Enter your rating: ");
        if (rating > 0) {
            currentUser.recordInteraction(selectedArticle, String.valueOf(rating));
            dbService.storeArticleRating(currentUser.getUserName(), selectedArticle.getId(), rating);
            recommendationEngine.updateUserPreference(currentUser);
        }
    } else if (choice == 0) {
        return;  // Go back to the main menu
    } else {
        printColoredText("Invalid choice. Please try again.", "\033[31m"); // Red color for invalid input
    }
}

   //To receive colored test in console to make it attractive
    public static void printColoredText(String text, String colorCode) {
        System.out.println(colorCode + text + "\033[0m");  // Reset to default color after printing
    }


    public static void printFormattedContent(String content) {
        int maxLineLength = 80; // Maximum line length before wrapping
        String[] words = content.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLineLength) {
                System.out.println(line.toString()); // Print the line
                line = new StringBuilder(word); // Start a new line with the current word
            } else {
                if (line.length() > 0) {
                    line.append(" "); // Add space between words
                }
                line.append(word);
            }
        }

        if (line.length() > 0) {
            System.out.println(line.toString());
        }
    }


    public static void getRecommendations() {
        dbService.loadPreferences(currentUser);
        dbService.loadReadingHistory(currentUser, availableArticles);
        if (currentUser.getPreferences().getAllPreferences().isEmpty()) {
            System.out.println("Since you're new, there are no recommendations yet. Please view some articles first.");
            return;
        }

        List<Article> recommendations = recommendationEngine.generateRecommendations(currentUser);
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available at the moment.");
        } else {
            System.out.println("Recommended Articles:");
            for (Article article : recommendations) {
                System.out.println(article.getTitle() + " - " + article.getCategory());
            }
            System.out.println("Do you want to view any of these articles? (Enter the number, or 0 to skip)");
        int choice = getIntInput("Your choice: ");
        if (choice > 0 && choice <= recommendations.size()) {
            Article selectedArticle = recommendations.get(choice - 1);
            System.out.println("\nFull Article Details:");
            System.out.println("Title: " + selectedArticle.getTitle());
            System.out.println("Source: " + selectedArticle.getSource());
            System.out.println("Published Date: " + selectedArticle.getPublishedDate());
            System.out.println("Category: " + selectedArticle.getCategory());
            System.out.println("\n" + selectedArticle.getContentOfArticle());
        } else if (choice == 0) {
            System.out.println("Returning to the main menu.");
        } else {
            System.out.println("Invalid choice. Returning to the main menu.");
        }
        }
    }

    public static void updatePreferences() {
        if (currentUser == null) {
            System.out.println("You need to login first.");
            return;
        }

        System.out.println("Update your preferences (e.g., Technology, Health, Sports, Entertainment, Social Issues, Politics, Science, Finance, Space, Environment, AI):");
        String categoryStr = scanner.nextLine();
        try {
            Category category = Category.valueOf(categoryStr.trim().toUpperCase().replace(" ", "_"));
            currentUser.getPreferences().setPreference(category, 3); // Set preference score
             dbService.storePreferences(currentUser);
              recommendationEngine.updateUserPreference(currentUser);
              // Update preferences in DB
            System.out.println("Preferences updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category. Please try again.");
        }
    }

    public static void logOut() {
        if (currentUser != null) {
            currentUser = null;
            System.out.println("Logged out successfully.");
        } else {
            System.out.println("No user logged in.");
        }
    }

     public static void setDbService(DatabaseService dbService) {
        MainApp.dbService = dbService;
    }

    public static void setRecommendationEngine(RecommendationEngine recommendationEngine) {
        MainApp.recommendationEngine = recommendationEngine;
    }

    public static void setAvailableArticles(List<Article> articles) {
    MainApp.availableArticles = articles;
}

}
