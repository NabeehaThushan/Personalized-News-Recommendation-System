package test.newsapp;

import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.UserPreference;
import main.newsapp.utils.DatabaseService;

import main.newsapp.models.User;
import main.newsapp.utils.DatabaseHandler;
import main.newsapp.utils.DatabaseService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;  // Use @BeforeEach in JUnit 5
import org.junit.jupiter.api.Test;        // Use @Test in JUnit 5
import static org.junit.jupiter.api.Assertions.*;  // For assertions
import static org.mockito.Mockito.*;
import java.util.Date;

public class UserTest {
    private User user;

    @Mock
    private DatabaseService dbServiceMock;

    // Set up a test user before each test
    @BeforeEach
    public void setUp() throws UserNotFoundException, IncorrectPasswordException {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        user = new User("testUser", "testPassword");

        Mockito.when(dbServiceMock.userExists("testUser")).thenReturn(true);
        Mockito.when(dbServiceMock.validateUser("testUser", "testPassword")).thenReturn(true);
        Mockito.when(dbServiceMock.validateUser("testUser", "wrongPassword")).thenReturn(false);
    }

    @Test
    public void testLogin() throws UserNotFoundException, IncorrectPasswordException {
        // Simulate successful login
        boolean loginSuccessful = user.login(dbServiceMock);

        // Assert that login was successful
        assertTrue(loginSuccessful, "User should log in successfully");
    }



    @Test
    public void testAddToReadingHistory() {
        // Create a mock Article
        Article article = new Article("Sample Title", "Sample Content", "Content", Category.TECHNOLOGY, "TestSource", new Date());

        // Add article to reading history
        user.addToReadingHistory(article);

        // Verify the reading history now includes the article
        assertEquals(1, user.getReadingHistory().size(), "Reading history should have 1 article");
        assertEquals("Sample Title", user.getReadingHistory().get(0).getTitle(), "First article title should match");
    }

    @Test
    public void testUpdatePreferences() {
        UserPreference newPreferences = new UserPreference();
        newPreferences.setPreference(Category.TECHNOLOGY, 5);

        // Update user preferences
        user.updatePreferences(newPreferences);

        // Verify that preferences were updated
        assertEquals(5, user.getPreferences().getAllPreferences().get(Category.TECHNOLOGY), "Preference score for TECHNOLOGY should be 5");
    }
}
