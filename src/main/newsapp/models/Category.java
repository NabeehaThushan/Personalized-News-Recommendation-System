package main.newsapp.models;

import java.util.Arrays;
import java.util.List;

public enum Category {
    TECHNOLOGY("Technology", Arrays.asList("technology", "ai", "machine learning", "innovation", "robotics", "customer service")),
    HEALTH("Health", Arrays.asList("health", "wellness", "mental", "fitness", "nutrition", "medicine")),
    SPORTS("Sports", Arrays.asList("sports", "football", "basketball", "cricket", "soccer", "athletics")),
    AI("Artificial Intelligence", Arrays.asList("artificial intelligence", "deep learning", "neural networks", "automation")),
    BUSINESS("Business", Arrays.asList("economy", "stocks", "finance", "business", "startups", "investment")),
    ENTERTAINMENT("Entertainment", Arrays.asList("movies", "celebrities", "music", "entertainment", "television", "gaming")),
    POLITICS("Politics", Arrays.asList("election", "government", "policy", "politics", "voting", "democracy")),
    SCIENCE("Science", Arrays.asList("quantum", "physics", "biology", "research", "chemistry", "astronomy")),
    GENERAL("General", Arrays.asList("news", "updates", "headlines", "latest")), // General category for miscellaneous topics
    EDUCATION("Education", Arrays.asList("learning", "schools", "courses", "teaching", "college", "university", "online learning")),
    SPACE("Space", Arrays.asList("space", "mars", "planets", "nasa", "astronomy", "star", "rocket", "exploration")),
    FINANCE("Finance", Arrays.asList("economy", "cryptocurrency", "stocks", "market", "financial", "investment", "banks")),
    SOCIAL_ISSUES("Social Issues", Arrays.asList("human rights", "equality", "justice", "poverty", "climate change", "community", "leadership")),
    ENVIRONMENT("Environment", Arrays.asList("climate change", "sustainability", "green energy", "recycling", "pollution", "conservation"));

    private final String displayName;
    private final List<String> keywords;

    Category(String displayName, List<String> keywords) {
        this.displayName = displayName;
        this.keywords = keywords;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}

