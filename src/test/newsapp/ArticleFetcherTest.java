package test.newsapp;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.utils.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleFetcherTest {

    private FileHandler fileHandlerMock; // Mocked FileHandler
    private List<Article> mockArticles; // Mock articles for testing

    @BeforeEach
    public void setUp() {
        // Initialize mock FileHandler
        fileHandlerMock = mock(FileHandler.class);

        // Create mock articles for testing
        mockArticles = List.of(
                new Article("AI Advancements", "Exploring AI and machine learning.", "content",
                        Category.TECHNOLOGY, "Tech Source", new Date()),
                new Article("Health Tips", "How to stay healthy during the winter.", "content",
                        Category.HEALTH, "Health Source", new Date()),
                new Article("Sports Highlights", "Latest sports updates.", "content",
                        Category.SPORTS, "Sports Source", new Date())
        );

        // Stub FileHandler behavior
        when(fileHandlerMock.fetchArticlesFromFile()).thenReturn(mockArticles);
    }

    @Test
    public void testFetchArticlesFromFile() {
        // Act: Call the mocked method
        List<Article> fetchedArticles = fileHandlerMock.fetchArticlesFromFile();

        // Assert: Verify that the articles were fetched correctly
        assertNotNull(fetchedArticles, "Fetched articles should not be null.");
        assertEquals(3, fetchedArticles.size(), "The fetched articles list size should be 3.");
        assertEquals("AI Advancements", fetchedArticles.get(0).getTitle(),
                "The title of the first article should match the mock data.");
    }

    @Test
    public void testFetchEmptyArticleList() {
        // Mock behavior for an empty article list
        when(fileHandlerMock.fetchArticlesFromFile()).thenReturn(List.of());

        // Act: Call the mocked method
        List<Article> fetchedArticles = fileHandlerMock.fetchArticlesFromFile();

        // Assert: Verify the fetched list is empty
        assertNotNull(fetchedArticles, "Fetched articles should not be null (even if empty).");
        assertTrue(fetchedArticles.isEmpty(), "The fetched articles list should be empty.");
    }

    @Test
    public void testFileHandlerInteraction() {
        // Act: Fetch articles
        fileHandlerMock.fetchArticlesFromFile();

        // Assert: Verify that the fetch method was called exactly once
        verify(fileHandlerMock, times(1)).fetchArticlesFromFile();
    }
}
