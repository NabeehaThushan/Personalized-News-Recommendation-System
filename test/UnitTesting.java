import main.newsapp.models.Article;
import main.newsapp.models.User;
import main.newsapp.services.ArticleFetcher;
import main.newsapp.utils.DatabaseHandler;
import main.newsapp.utils.FileHandler;

import java.util.List;

public class UnitTesting {
    public static void main(String[] args) {
        // Test FileHandler.fetchArticlesFromFile()
        testFetchArticlesFromFile();

        // Test User Registration
        testUserRegistration();

        // Test Categorizing Articles
        testCategorizeArticles();
    }

    private static void testFetchArticlesFromFile() {
        FileHandler fileHandler = new FileHandler();
        try {
            List<Article> articles = fileHandler.fetchArticlesFromFile();
            if (articles.isEmpty()) {
                System.out.println("Test Fetch Articles: FAILED - No articles fetched.");
            } else {
                System.out.println("Test Fetch Articles: PASSED - " + articles.size() + " articles fetched.");
            }
        } catch (Exception e) {
            System.out.println("Test Fetch Articles: FAILED - Exception occurred: " + e.getMessage());
        }
    }

    private static void testUserRegistration() {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user = new User("testUser", "password123");
        try {
            user.register(dbHandler);
            System.out.println("Test User Registration: PASSED.");
        } catch (Exception e) {
            System.out.println("Test User Registration: FAILED - " + e.getMessage());
        }
    }

    private static void testCategorizeArticles() {
        ArticleFetcher fetcher = new ArticleFetcher();
        fetcher.getArticles(); // Fetch and categorize articles
        for (Article article : fetcher.getArticles()) {
            if (article.getCategory() != null) {
                System.out.println("Test Categorize Articles: PASSED for " + article.getTitle() + " - " + article.getCategory());
            } else {
                System.out.println("Test Categorize Articles: FAILED for " + article.getTitle());
            }
        }
    }
}

