package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.UserPreference;
import org.junit.Before;
import org.junit.Test;
import main.newsapp.models.User;

import java.util.Date;

import static org.junit.Assert.*;

public class UserTest {
    private User user;

    // Set up a test user before each test
    @Before
    public void setUp() {
        user = new User("testUser", "testPassword");
    }

    @Test
    public void testLogin() {
        // Simulate successful login
        assertTrue("User should log in successfully", user.login());
    }

    @Test
    public void testAddToReadingHistory() {
        // Create a mock Article (assuming you have an Article class)
        Article article = new Article("Sample Title", "Sample Content", Category.TECHNOLOGY, "TestSource", new Date());

        // Add article to reading history
        user.addToReadingHistory(article);

        // Verify the reading history now includes the article
        assertEquals("Reading history should have 1 article", 1, user.getReadingHistory().size());
        assertEquals("First article title should match", "Sample Title", user.getReadingHistory().get(0).getTitle());
    }

    @Test
    public void testUpdatePreferences() {
        UserPreference newPreferences = new UserPreference();
        newPreferences.setPreference(Category.TECHNOLOGY, 5);

        // Update user preferences
        user.updatePreferences(newPreferences);

        // Verify that preferences were updated
        assertEquals("Preference score for TECHNOLOGY should be 5", 5, (int) user.getPreferences().getAllPreferences().get(Category.TECHNOLOGY));
    }
}
