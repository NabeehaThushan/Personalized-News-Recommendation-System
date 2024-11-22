package test.newsapp;

import main.newsapp.models.Admin;
import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {
    private Admin admin;
    private List<Article> articles;
    private User user;

    @BeforeEach
    public void setUp() {
        admin = new Admin("adminUser", "adminPassword");
        articles = new ArrayList<>();

        // Sample articles
        Article article1 = new Article("AI Innovations", "Content about AI", Category.TECHNOLOGY, "Tech Source", new Date());
        Article article2 = new Article("Health Tips", "Content about health", Category.HEALTH, "Health Source", new Date());
        articles.add(article1);
        articles.add(article2);

        // Sample user
        user = new User("testUser", "password123");
        user.addToReadingHistory(article1);
        user.getPreferences().setPreference(Category.TECHNOLOGY, 5);
    }

    @Test
    public void testAddArticle() {
        Article newArticle = new Article("Sports Highlights", "Content about sports", Category.SPORTS, "Sports Source", new Date());
        admin.addArticle(articles, newArticle);

        assertTrue(articles.contains(newArticle));
    }

    @Test
    public void testAddDuplicateArticle() {
        Article duplicateArticle = articles.get(0); // Get an existing article
        admin.addArticle(articles, duplicateArticle);

        assertEquals(2, articles.size()); // Should not add duplicate
    }

    @Test
    public void testRemoveArticle() {
        String articleId = articles.get(0).getId();
        admin.removeArticle(articles, articleId);

        assertEquals(1, articles.size()); // One article removed
        assertFalse(articles.stream().anyMatch(article -> article.getId().equals(articleId))); // Verify removal
    }

    @Test
    public void testRemoveNonExistentArticle() {
        String nonExistentId = "nonexistent-id";
        admin.removeArticle(articles, nonExistentId);

        assertEquals(2, articles.size()); // No changes
    }

    @Test
    public void testViewUserActivity() {
        // For view methods, assert statements are less relevant but we can check for no exceptions.
        assertDoesNotThrow(() -> admin.viewUserActivity(user));
    }

    @Test
    public void testResetUserPreferences() {
        admin.resetUserPreferences(user);

        assertTrue(user.getPreferences().getAllPreferences().isEmpty()); // Preferences reset
    }

    @Test
    public void testListAllArticles() {
        // For list methods, assert statements are less relevant but we can check for no exceptions.
        assertDoesNotThrow(() -> admin.listAllArticles(articles));
    }
}
