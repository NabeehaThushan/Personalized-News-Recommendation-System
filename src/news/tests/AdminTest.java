package news.tests;

import newsapp.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminTest {
    public static void main(String[] args) {
        List<Article> articles = new ArrayList<>();

        // Sample Articles
        Article article1 = new Article("Tech Trends", "Content about technology.", Category.TECHNOLOGY, "TechSource", new Date());
        Article article2 = new Article("Health Tips", "Content about health.", Category.HEALTH, "HealthSource", new Date());

        // Add articles to the list for testing
        articles.add(article1);
        articles.add(article2);

        // Create an Admin and a User
        Admin admin = new Admin("adminUser", "adminPass");
        User user = new User("johnDoe", "password123");

        // User interacts with articles
        user.addToReadingHistory(article1);
        user.updatePreferences(new UserPreference());

        // Admin adds a new article
        Article newArticle = new Article("New AI Article", "AI advances", Category.AI, "AISource", new Date());
        admin.addArticle(articles, newArticle);

        // Admin removes an article
        admin.removeArticle(articles, article1.getId());

        // Admin views user activity
        admin.viewUserActivity(user);

        // Admin resets user preferences
        admin.resetUserPreferences(user);

        // Admin lists all articles
        admin.listAllArticles(articles);
    }
}
