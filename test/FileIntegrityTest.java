import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.utils.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

public class FileIntegrityTest {

    private static final String TEST_FILE_PATH = "C:\\Users\\nabee\\IdeaProjects\\PersonalizedNewsRecommendationSystem\\src\\test-article.json";
    private static final String BACKUP_FILE_PATH = "C:\\Users\\nabee\\IdeaProjects\\PersonalizedNewsRecommendationSystem\\src\\articles_backup.json";
    private static FileHandler fileHandler = new FileHandler();

    public static void main(String[] args) {
        System.out.println("Running File Integrity Tests...\n");

        testFileReadability();
        testBackupFileCreation();
        testJSONFormatConsistency();
        testDatabaseFileConnectivity();
        testSavingArticlesToFile();
    }

    public static void testFileReadability() {
        System.out.println("Test: File Readability");

        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists() && testFile.canRead()) {
            System.out.println("PASS: Test file exists and is readable.");
        } else {
            System.out.println("FAIL: Test file does not exist or is not readable.");
        }
    }

    public static void testBackupFileCreation() {
        System.out.println("\nTest: Backup File Creation");

        try {
            File originalFile = new File(TEST_FILE_PATH);
            if (!originalFile.exists()) {
                System.out.println("FAIL: Original file does not exist.");
                return;
            }

            // Perform backup
            FileHandler.backupArticlesFile();

            // Verify backup file is created
            File backupFile = new File(BACKUP_FILE_PATH);
            if (backupFile.exists() && backupFile.length() > 0) {
                System.out.println("PASS: Backup file created successfully.");
            } else {
                System.out.println("FAIL: Backup file not created or is empty.");
            }

            // Compare contents of original and backup file
            String originalContent = Files.readString(originalFile.toPath());
            String backupContent = Files.readString(backupFile.toPath());
            if (originalContent.equals(backupContent)) {
                System.out.println("PASS: Backup file content matches the original.");
            } else {
                System.out.println("FAIL: Backup file content does not match the original.");
            }

        } catch (IOException e) {
            System.out.println("FAIL: Exception occurred while creating backup - " + e.getMessage());
        }
    }

    public static void testJSONFormatConsistency() {
        System.out.println("\nTest: JSON Format Consistency");

        try {
            List<Article> articles = fileHandler.fetchArticlesFromFile();
            if (articles != null && !articles.isEmpty()) {
                System.out.println("PASS: JSON file parsed successfully. Articles found: " + articles.size());
            } else {
                System.out.println("FAIL: JSON file parsing failed or no articles found.");
            }
        } catch (Exception e) {
            System.out.println("FAIL: Exception occurred during JSON parsing - " + e.getMessage());
        }
    }

    public static void testDatabaseFileConnectivity() {
        System.out.println("\nTest: Database File Connectivity");

        File databaseFile = new File("src/user_data.db");
        if (databaseFile.exists() && databaseFile.canRead()) {
            System.out.println("PASS: Database file exists and is readable.");
        } else {
            System.out.println("FAIL: Database file does not exist or is not readable.");
        }
    }

    public static void testSavingArticlesToFile() {
        System.out.println("\nTest: Saving Articles to File");

        try {
            // Create mock articles
            List<Article> mockArticles = List.of(
                    new Article("AI Innovations", "Exploring AI", "content", Category.TECHNOLOGY, "Tech Source", new Date()),
                    new Article("Health Tips", "How to stay healthy", "content", Category.HEALTH, "Health Source", new Date())
            );

            // Save articles to the test file
            fileHandler.saveArticlesToFile(mockArticles);

            // Fetch the articles from the file
            List<Article> savedArticles = fileHandler.fetchArticlesFromFile();

            if (savedArticles != null && savedArticles.size() == 2) {
                System.out.println("PASS: Articles saved and retrieved successfully.");
                System.out.println("Saved Article Titles: ");
                savedArticles.forEach(article -> System.out.println("- " + article.getTitle()));
            } else {
                System.out.println("FAIL: Articles not saved correctly.");
            }
        } catch (Exception e) {
            System.out.println("FAIL: Exception occurred while saving articles - " + e.getMessage());
        }
    }
}
