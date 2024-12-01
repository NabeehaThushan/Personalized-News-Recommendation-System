package test.newsapp;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.main.MainApp;
import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import main.newsapp.services.RecommendationEngine;
import main.newsapp.utils.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainAppTest {

    @Mock
    private DatabaseService mockDbService;

    @Mock
    private RecommendationEngine mockRecommendationEngine;

    private List<Article> mockArticles;

    @BeforeEach
public void setUp() {
    MockitoAnnotations.openMocks(this);

    MainApp.setDbService(mockDbService);
    MainApp.setRecommendationEngine(mockRecommendationEngine);

    // Mock articles for recommendations
    mockArticles = List.of(
            new Article("AI Innovations", "Exploring AI advancements", "content", Category.TECHNOLOGY, "Tech Source", new Date()),
            new Article("Health Tips", "How to stay healthy", "content", Category.HEALTH, "Health Source", new Date())
    );

    when(mockRecommendationEngine.generateRecommendations(any(User.class))).thenReturn(mockArticles);
}

    @Test
    public void testSimulateUserActions_LoginSuccess() throws Exception {
        when(mockDbService.userExists("User1")).thenReturn(true);
        when(mockDbService.validateUser("User1", "password1")).thenReturn(true);

        // Simulate user actions
        assertDoesNotThrow(() -> MainApp.simulateUserActions("User1", "password1"));

        // Verify calls
        verify(mockDbService).loadPreferences(any(User.class));
        verify(mockDbService).loadReadingHistory(any(User.class), anyList());
        verify(mockRecommendationEngine).generateRecommendations(any(User.class));
    }


 @Test
    public void testPerformUserActions() throws Exception {
        User mockUser = new User("User1", "password1");

        assertDoesNotThrow(() -> MainApp.performUserActions(mockUser));
        verify(mockRecommendationEngine).generateRecommendations(mockUser);
    }

    @Test
    public void testRegisterNewUser() {
        // Simulate a new user registration
        User newUser = new User("NewUser", "password123");

        // Simulate registration logic
        when(mockDbService.userExists("NewUser")).thenReturn(false);

        assertDoesNotThrow(() -> {
            newUser.register(mockDbService);
        });

        verify(mockDbService).storePreferences(newUser);
    }

    @Test
    public void testRegisterDuplicateUser() throws Exception {
        when(mockDbService.userExists("ExistingUser")).thenReturn(true);

        User duplicateUser = new User("ExistingUser", "password456");

        assertThrows(DuplicateUserException.class, () -> {
            duplicateUser.register(mockDbService);
        });

        verify(mockDbService, never()).storePreferences(duplicateUser);
    }
}
