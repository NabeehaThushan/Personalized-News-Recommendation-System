package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.services.RecommendationEngine;
import main.newsapp.utils.DatabaseHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationEngineTest {

    private RecommendationEngine recommendationEngine;

    @BeforeEach
    public void setUp() {
        // Use a mock database handler for testing
        DatabaseHandler mockDatabaseHandler = new DatabaseHandler();

        // Sample articles to use in the recommendation engine
        List<Article> mockArticles = List.of(
                new Article("AI Advancements", "Exploring AI and machine learning.", Category.TECHNOLOGY, "Tech Source", new Date()),
                new Article("Health Tips", "How to stay healthy during the winter.", Category.HEALTH, "Health Source", new Date())
        );

        // Initialize the recommendation engine with mock data
        recommendationEngine = new RecommendationEngine(mockArticles, mockDatabaseHandler);
    }

    @Test
    public void testPredictArticlePriority() {
        // Create a sample article
        Article article = new Article(
                "Quantum Computing Breakthrough",
                "Quantum computing is changing technology.",
                Category.TECHNOLOGY,
                "Tech Daily",
                new Date()
        );

        // Call the method
        int priority = recommendationEngine.predictArticlePriority(article);

        // Assert the priority is in the expected range
        assertTrue(priority > 0, "Priority should be greater than 0.");
        System.out.println("Predicted priority: " + priority);
    }
}
