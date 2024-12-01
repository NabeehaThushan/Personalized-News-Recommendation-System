package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.utils.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileHandlerTest {

    private FileHandler fileHandler;
    private static final String TEST_FILE_PATH = "src/test/resources/test_articles.json";
    private static final String TEST_BACKUP_FILE_PATH = "src/test/resources/test_articles_backup.json";

    @BeforeEach
    public void setUp() {
        // Initialize the FileHandler with a test file path.
        fileHandler = new FileHandler();
    }

    @Test
    public void testFetchArticlesFromFile() throws IOException {
        // Prepopulate a test file with mock articles.
        List<Article> mockArticles = List.of(
                new Article("AI Innovations", "Exploring AI", "content", Category.TECHNOLOGY, "Tech Source", new java.util.Date()),
                new Article("Health Tips", "How to stay healthy", "content", Category.HEALTH, "Health Source", new java.util.Date())
        );

        // Save articles to the test file
        fileHandler.saveArticlesToFile(mockArticles);

        // Fetch articles from the file
        List<Article> articles = fileHandler.fetchArticlesFromFile();

        // Verify the articles are correctly fetched
        assertNotNull(articles, "Articles should not be null");
        assertEquals(2, articles.size(), "There should be 2 articles");
        assertEquals("AI Innovations", articles.get(0).getTitle(), "First article title should match");
    }

    @Test
    public void testSaveArticlesToFile() throws IOException {
        // Create a list of articles to save
        List<Article> articles = List.of(
                new Article("AI Innovations", "Exploring AI", "content", Category.TECHNOLOGY, "TechSource", new java.util.Date()),
                new Article("Health Tips", "How to stay healthy", "content", Category.HEALTH, "HealthSource", new java.util.Date())
        );

        // Save articles to the file
        fileHandler.saveArticlesToFile(articles);

        // Fetch the articles from the file
        List<Article> fetchedArticles = fileHandler.fetchArticlesFromFile();

        // Verify that the articles were saved correctly
        assertNotNull(fetchedArticles, "Articles should not be null");
        assertEquals(2, fetchedArticles.size(), "There should be 2 articles saved");
        assertEquals("AI Innovations", fetchedArticles.get(0).getTitle(), "First article title should match after saving");
    }

    @Test
    public void testParseDate() {
        String validDateString = "2022-01-01";
        String invalidDateString = "invalid-date";

        // Test valid date parsing
        Date validDate = fileHandler.parseDate(validDateString);
        assertNotNull(validDate, "Valid date string should be parsed successfully");

        // Test invalid date parsing
        Date invalidDate = fileHandler.parseDate(invalidDateString);
        assertNotNull(invalidDate, "Invalid date string should fallback to default date");
    }
}
