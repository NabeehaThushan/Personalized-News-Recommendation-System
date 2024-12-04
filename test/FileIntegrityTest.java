import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.utils.FileHandler;

import java.util.List;

public class FileIntegrityTest {
    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();

        // Test Saving Articles to File
        List<Article> articles = List.of(
                new Article("AI Innovations", "Exploring AI", "content", Category.TECHNOLOGY, "Tech Source", new java.util.Date()),
                new Article("Health Tips", "How to stay healthy", "content", Category.HEALTH, "Health Source", new java.util.Date())
        );
        testSaveArticlesToFile(fileHandler, articles);

        // Test Fetching Articles from File
        testFetchArticlesFromFile(fileHandler);

        // Test Backup File
        testBackupFile(fileHandler);
    }

    private static void testSaveArticlesToFile(FileHandler fileHandler, List<Article> articles) {
        try {
            fileHandler.saveArticlesToFile(articles);
            System.out.println("Test Save Articles: PASSED.");
        } catch (Exception e) {
            System.out.println("Test Save Articles: FAILED - " + e.getMessage());
        }
    }

    private static void testFetchArticlesFromFile(FileHandler fileHandler) {
        try {
            List<Article> fetchedArticles = fileHandler.fetchArticlesFromFile();
            if (fetchedArticles.isEmpty()) {
                System.out.println("Test Fetch Articles: FAILED - No articles fetched.");
            } else {
                System.out.println("Test Fetch Articles: PASSED - " + fetchedArticles.size() + " articles fetched.");
            }
        } catch (Exception e) {
            System.out.println("Test Fetch Articles: FAILED - " + e.getMessage());
        }
    }

    private static void testBackupFile(FileHandler fileHandler) {
        try {
            fileHandler.backupArticlesFile();
            System.out.println("Test Backup File: PASSED.");
        } catch (Exception e) {
            System.out.println("Test Backup File: FAILED - " + e.getMessage());
        }
    }
}
