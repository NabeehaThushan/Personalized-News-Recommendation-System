package main.newsapp.models;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;

public class UserPreference {
    private Map<Category, Integer> categoryPreferences;

    public UserPreference() {
        this.categoryPreferences = new HashMap<>();
    }

    public void addCategory(Category category) {
        categoryPreferences.put(category, categoryPreferences.getOrDefault(category, 0) + 1);
    }

    public Category getTopPreference() {
        return categoryPreferences.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Map<Category, Integer> getAllPreferences() {
        return categoryPreferences;
    }

    public void setPreference(Category category, int score) {
        categoryPreferences.put(category, score);
    }

    public String toString() {
        return categoryPreferences.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .reduce((pref1, pref2) -> pref1 + ", " + pref2)
                .orElse("No preferences set");
    }

    public void setAllPreferences(Map<Category, Integer> preferences) {
        this.categoryPreferences = preferences;
    }

    public int getPreferenceScore(Category category) {
    return categoryPreferences.getOrDefault(category, 0);
}

}