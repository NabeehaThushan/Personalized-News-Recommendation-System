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
        // Sample articles
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Tech Innovations", "Content about technology", Category.TECHNOLOGY, "TechSource", new Date()));
        articles.add(new Article("AI Breakthrough", "Content about AI", Category.AI, "AISource", new Date()));
        articles.add(new Article("Health Benefits of Exercise", "Content about health", Category.HEALTH, "HealthSource", new Date()));

        // User with preferences
        User user = new User("johnDoe", "password123");
        user.getPreferences().setPreference(Category.TECHNOLOGY, 5);
        user.getPreferences().setPreference(Category.AI, 3);
        user.getPreferences().setPreference(Category.HEALTH, 1);

        // Recommendation engine
        RecommendationEngine engine = new RecommendationEngine(articles);

        // Generate recommendations
        List<Article> recommendations = engine.generateRecommendations(user);

        // Print recommendations
        System.out.println("Recommended Articles:");
        for (Article article : recommendations) {
            System.out.println(article.getTitle() + " - Category: " + article.getCategory());
        }
    }
}
