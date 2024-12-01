package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
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
                new Article("AI Advancements", "Exploring AI and machine learning.","content", Category.TECHNOLOGY, "Tech Source", new Date()),
                new Article("Health Tips", "How to stay healthy during the winter.","content" ,Category.HEALTH, "Health Source", new Date())
        );

        // Initialize the recommendation engine with mock data
        recommendationEngine = new RecommendationEngine(mockArticles, mockDatabaseHandler);
    }

   @Test
        public void testGenerateRecommendations() {
            // Mock user preferences
            User user = new User("testUser", "password");
            user.getPreferences().setPreference(Category.TECHNOLOGY, 5);
            user.getPreferences().setPreference(Category.HEALTH, 3);

            // Generate recommendations
            List<Article> recommendations = recommendationEngine.generateRecommendations(user);

            // Assert recommendations are based on preferences
            assertNotNull(recommendations, "Recommendations should not be null.");
            assertFalse(recommendations.isEmpty(), "Recommendations should not be empty.");
            assertEquals(Category.TECHNOLOGY, recommendations.get(0).getCategory(),
                "First recommendation should match the highest preference.");
    }

}
