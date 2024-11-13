package news.tests;

import newsapp.models.Article;
import newsapp.models.Category;
import newsapp.models.User;
import newsapp.services.RecommendationEngine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RecommendationEngineTest {

    public static void main(String[] args) {
        testRecommendationEngine();
    }

    public static void testRecommendationEngine() {
        // Sample articles
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("AI in Technology", "Exploring AI advancements", Category.TECHNOLOGY, "Tech World", new Date()));
        articles.add(new Article("Health Benefits of Yoga", "Wellness and mental health", Category.HEALTH, "Health Digest", new Date()));
        articles.add(new Article("Football World Cup Highlights", "Best moments of the season", Category.SPORTS, "Sports Daily", new Date()));
        articles.add(new Article("Quantum Physics Discoveries", "Exploring quantum realms", Category.SCIENCE, "Science World", new Date()));

        // User setup with reading history
        User user = new User("johnDoe", "password123");
        user.addToReadingHistory(new Article("Advances in AI", "Latest trends in AI and machine learning", Category.TECHNOLOGY, "AI Journal", new Date()));
        user.addToReadingHistory(new Article("Meditation Benefits", "Mental wellness through meditation", Category.HEALTH, "Wellness Weekly", new Date()));

        // Initialize the recommendation engine with available articles
        RecommendationEngine engine = new RecommendationEngine(articles);

        // Test user preference adaptation
        engine.updateUserPreference(user);
        System.out.println("Updated user preferences:");
        user.getPreferences().getAllPreferences().forEach((category, score) ->
            System.out.println(category + ": " + score)
        );

        // Generate recommendations
        List<Article> recommendations = engine.generateRecommendations(user);

        // Verify recommendations output
        System.out.println("\nRecommended Articles for " + user.getUserName() + ":");
        for (Article article : recommendations) {
            System.out.println("Title: " + article.getTitle() + " | Category: " + article.getCategory());
        }

        // Shut down the engine after tests
        engine.shutDown();
    }
}
