package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.services.ArticleFetcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleFetcherTest {
    private ArticleFetcher articleFetcher;
    private List<Article> testArticles;

    @BeforeEach
    public void setUp() {
        // Initialize ArticleFetcher
        articleFetcher = new ArticleFetcher();

        // Create mock articles for testing
        testArticles = new ArrayList<>();
        testArticles.add(new Article("AI Advances", "Advancements in technology and AI are remarkable.", Category.GENERAL, "Tech Source", new Date()));
        testArticles.add(new Article("Health Tips", "Mental health and wellness for everyone.", Category.GENERAL, "Health Source", new Date()));
        testArticles.add(new Article("Football Highlights", "Best moments of the football season.", Category.GENERAL, "Sports Source", new Date()));
        testArticles.add(new Article("Quantum Physics Discovery", "Recent breakthroughs in quantum mechanics.", Category.GENERAL, "Science Daily", new Date()));

        // Inject mock articles into the ArticleFetcher
        articleFetcher.setArticles(testArticles);
    }

    @Test
    public void testCategorizeArticlesWithNLP() {
        // Perform categorization
        articleFetcher.categorizeArticlesWithNLP();

        // Assert categories are correctly assigned
        assertEquals(Category.TECHNOLOGY, testArticles.get(0).getCategory(), "The first article should be categorized as TECHNOLOGY.");
        assertEquals(Category.HEALTH, testArticles.get(1).getCategory(), "The second article should be categorized as HEALTH.");
        assertEquals(Category.SPORTS, testArticles.get(2).getCategory(), "The third article should be categorized as SPORTS.");
        assertEquals(Category.SCIENCE, testArticles.get(3).getCategory(), "The fourth article should be categorized as SCIENCE.");
    }

    @Test
    public void testGetArticlesFromFile() {
        // Verify that getArticles fetches articles from the JSON file
        List<Article> articles = articleFetcher.getArticles();

        assertNotNull(articles, "The list of articles should not be null.");
        assertTrue(articles.size() > 0, "The list of articles should contain data fetched from the JSON file.");
    }

    @Test
    public void testContainsKeywords() {
        // Simulate tokenized content
        String[] tokens = {"quantum", "physics", "breakthrough"};
        String[] keywords = {"quantum", "science", "physics"};

        // Temporarily expose containsKeywords to test logic directly (or use a proxy if private)
        boolean contains = articleFetcher.containsKeywords(tokens, keywords);
        assertTrue(contains, "The helper method should detect matching keywords.");
    }
}
