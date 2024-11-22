package test.newsapp;

import main.newsapp.models.Category;
import main.newsapp.models.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserPrefTest {
    private UserPreference userPreference;

    @BeforeEach
    public void setUp() {
        userPreference = new UserPreference();
    }

    @Test
    public void testAddCategory() {
        userPreference.addCategory(Category.TECHNOLOGY);
        userPreference.addCategory(Category.TECHNOLOGY);

        assertEquals(2, userPreference.getAllPreferences().get(Category.TECHNOLOGY));
    }

    @Test
    public void testGetTopPreference() {
        userPreference.setPreference(Category.TECHNOLOGY, 5);
        userPreference.setPreference(Category.HEALTH, 3);
        userPreference.setPreference(Category.SPORTS, 7);

        Category topCategory = userPreference.getTopPreference();
        assertEquals(Category.SPORTS, topCategory);
    }

    @Test
    public void testSetPreference() {
        userPreference.setPreference(Category.HEALTH, 4);
        assertEquals(4, userPreference.getAllPreferences().get(Category.HEALTH));
    }

    @Test
    public void testSetAllPreferences() {
        Map<Category, Integer> newPreferences = new HashMap<>();
        newPreferences.put(Category.TECHNOLOGY, 3);
        newPreferences.put(Category.HEALTH, 2);

        userPreference.setAllPreferences(newPreferences);

        assertEquals(2, userPreference.getAllPreferences().size());
        assertEquals(3, userPreference.getAllPreferences().get(Category.TECHNOLOGY));
    }

    @Test
    public void testToString() {
        userPreference.setPreference(Category.TECHNOLOGY, 5);
        String expected = "UserPreference{categoryPreferences={TECHNOLOGY=5}}";
        assertEquals(expected, userPreference.toString());
    }

    @Test
    public void testGetAllPreferencesEmpty() {
        assertTrue(userPreference.getAllPreferences().isEmpty());
    }
}
