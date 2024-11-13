package news.tests;

import newsapp.models.Article;
import newsapp.models.Category;
import newsapp.services.ArticleFetcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleFetcherTest {

    public static void main(String[] args) {
        // Initialize test articles
        List<Article> testArticles = new ArrayList<>();
        testArticles.add(new Article("AI Breakthroughs", "The latest advancements in machine learning and AI technology.", Category.GENERAL, "Tech World", null));
        testArticles.add(new Article("Health Tips", "Learn about wellness and mental health.", Category.GENERAL, "Health Digest", null));
        testArticles.add(new Article("Football Highlights", "The top football moments this season.", Category.GENERAL, "Sports Daily", null));
        testArticles.add(new Article("Quantum Discoveries", "New discoveries in quantum physics.", Category.GENERAL, "Science Today", null));

        // Instantiate ArticleFetcher with the test articles
        ArticleFetcher fetcher = new ArticleFetcher();
        fetcher.setArticles(testArticles);  // Assuming a setter for testing purposes

        // Run the categorization method
        fetcher.categorizeArticlesWithNLP();

        // Verify the categorization
        for (Article article : fetcher.getArticles()) {
            System.out.println("Title: " + article.getTitle());
            System.out.println("Expected Category: " + expectedCategory(article.getTitle()));
            System.out.println("Assigned Category: " + article.getCategory());
            System.out.println("----");
        }

        // Check if the assigned categories match expectations
        for (Article article : fetcher.getArticles()) {
            if (article.getCategory() != expectedCategory(article.getTitle())) {
                System.out.println("Test failed for article: " + article.getTitle());
            } else {
                System.out.println("Test passed for article: " + article.getTitle());
            }
        }
    }

    // Helper method to define expected categories based on titles
    private static Category expectedCategory(String title) {
        if (title.toLowerCase().contains("ai") || title.toLowerCase().contains("machine learning")) {
            return Category.TECHNOLOGY;
        } else if (title.toLowerCase().contains("health") || title.toLowerCase().contains("wellness")) {
            return Category.HEALTH;
        } else if (title.toLowerCase().contains("football") || title.toLowerCase().contains("sports")) {
            return Category.SPORTS;
        } else if (title.toLowerCase().contains("quantum") || title.toLowerCase().contains("physics")) {
            return Category.SCIENCE;
        } else {
            return Category.GENERAL;
        }
    }
}
