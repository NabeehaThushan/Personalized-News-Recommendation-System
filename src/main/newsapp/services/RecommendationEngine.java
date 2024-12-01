package main.newsapp.services;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import main.newsapp.utils.DatabaseHandler;

import main.newsapp.utils.DatabaseService;
import opennlp.tools.tokenize.SimpleTokenizer;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static main.newsapp.models.User.executorService;

public class RecommendationEngine {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngine.class);

    private List<Article> availableArticles;
    private DatabaseService databaseService;
    private Classifier mlModel;
    private ExecutorService executorService;
     // Flag to toggle ML-based recommendation or not

    public RecommendationEngine(List<Article> availableArticles, DatabaseService databaseService) {
        this.availableArticles = availableArticles;
        this.databaseService = databaseService;
        this.executorService = Executors.newCachedThreadPool();
        processArticles();

    }


    // Update user preferences based on their reading history
    public void updateUserPreference(User user) {
//        Map<Category, Integer> preferences = user.getPreferences().getAllPreferences();
//        for (Article article : user.getReadingHistory()) {
//            Category category = categorizeWithNLP(article.getContentOfArticle());
//            preferences.merge(category, 1, Integer::sum);  // Increment preference score
//        }
//        user.getPreferences().setAllPreferences(preferences);
//        databaseService.storePreferences(user);
        for (Article article : user.getReadingHistory()) {
            user.getPreferences().addCategory(article.getCategory());
        }
        databaseService.storePreferences(user);

    }

    public void categorizeArticle(Article article) {
    if (article.getCategory() == null) { // If not already categorized
        String content = article.getContentOfArticle().toLowerCase();

        for (Category category : Category.values()) {
            if (category.getKeywords().stream().anyMatch(content::contains)) {
                article.setCategory(category);
                return; // Stop checking once a match is found
            }
        }

        // Assign General category if no match
        article.setCategory(Category.GENERAL);
    }
}

    private Category categorizeWithNLP(String content) {
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    String[] tokens = tokenizer.tokenize(content.toLowerCase());

    for (Category category : Category.values()) {
        if (containsKeywords(tokens, category.getKeywords())) {
            return category; // Return the first matching category
        }
    }

    return Category.GENERAL; // Default to General if no match
}

// Helper method to check if any tokens match a category's keywords
private boolean containsKeywords(String[] tokens, List<String> keywords) {
    for (String token : tokens) {
        if (keywords.contains(token)) {
            return true;
        }
    }
    return false;
}


    // Get articles based on user preferences (Content-based filtering)
    private List<Article> getArticlesByUserPreferences(User user) {
        Map<Category, Integer> sortedPreferences = user.getPreferences().getAllPreferences().entrySet().stream()
                .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        List<Article> recommendations = new ArrayList<>();
        for (Map.Entry<Category, Integer> entry : sortedPreferences.entrySet()) {
            Category preferredCategory = entry.getKey();
            recommendations.addAll(availableArticles.stream()
                    .filter(article -> article.getCategory() == preferredCategory)
                    .collect(Collectors.toList()));
        }
        return recommendations;
    }

    /**
     * Retrieves articles based on the user's reading history.
     *
     * @param user the user for whom articles are being fetched
     * @return a list of articles related to the user's reading history
     */
    private List<Article> getArticlesByUserHistory(User user) {
        Set<Category> historyCategories = user.getReadingHistory().stream()
                .map(Article::getCategory)
                .collect(Collectors.toSet());

        return availableArticles.stream()
                .filter(article -> historyCategories.contains(article.getCategory()))
                .collect(Collectors.toList());
    }

    public List<Article> generateRecommendations(User user) {
    // Load user preferences and history
    databaseService.loadPreferences(user);
    databaseService.loadReadingHistory(user, availableArticles);

    // Fetch user-specific ratings for prioritization
    Map<String, Integer> userRatings = databaseService.getUserRatings(user.getUserName());

    // Step 1: Handle fallback for new users (if no preferences or history)
    if (user.getPreferences().getAllPreferences().isEmpty() && userRatings.isEmpty()) {
        logger.info("No preferences found for user. Suggesting random articles.");
        return availableArticles.stream().limit(5).collect(Collectors.toList());
    }

    // Step 2: Get recommendations from preferences and history
    List<Article> contentBasedRecommendations = getArticlesByUserPreferences(user);
    List<Article> historyBasedRecommendations = getArticlesByUserHistory(user);

    // Step 3: Combine recommendations and remove duplicates
    Set<Article> combinedRecommendations = new LinkedHashSet<>();
    combinedRecommendations.addAll(historyBasedRecommendations); // Prioritize recent history
    combinedRecommendations.addAll(contentBasedRecommendations);

    // Step 4: Filter out articles already in the user's reading history
    List<Article> filteredRecommendations = combinedRecommendations.stream()
            .filter(article -> user.getReadingHistory().stream()
                    .noneMatch(read -> read.getId().equals(article.getId())))
            .collect(Collectors.toList());

    // Step 5: Limit recommendations to 5 and return
    return filteredRecommendations.stream()
            .distinct() // Ensure no duplicates
            .limit(7) // Limit recommendations to 5
            .collect(Collectors.toList());
}

    private List<Article> getArticlesByKNN(User user) {
    List<Article> recommendations = new ArrayList<>();
    Map<Category, Integer> preferences = user.getPreferences().getAllPreferences();

    preferences.entrySet().stream()
            .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
            .limit(3) // Consider top 3 categories
            .forEach(entry -> {
                Category category = entry.getKey();
                recommendations.addAll(availableArticles.stream()
                        .filter(article -> article.getCategory() == category)
                        .collect(Collectors.toList()));
            });

    return recommendations.stream()
            .distinct()
            .limit(5) // Limit recommendations to 5
            .collect(Collectors.toList());
}



    // Rank articles by ML model if available
    private List<Article> rankArticlesByML(List<Article> articles) {
        try {
            Instances dataset = loadArffDataset();
            for (Article article : articles) {
                Instance instance = new DenseInstance(dataset.numAttributes());
                instance.setValue(dataset.attribute("length"), article.getContentOfArticle().length());
                instance.setValue(dataset.attribute("category"), article.getCategory().name());
                dataset.add(instance);
                double priority = mlModel.classifyInstance(dataset.lastInstance());
                article.setCategory(Category.values()[(int) priority]); // Set article priority based on ML
            }
        } catch (Exception e) {
            logger.error("Error in ranking articles using ML model: {}", e.getMessage());
        }
        return articles;
    }

    // Load ARFF dataset for ML-based prediction
    private Instances loadArffDataset() throws Exception {
        File arffFile = new File("src/main/newsapp/resources/article-data.arff");
        Instances dataset = new Instances(new FileReader(arffFile));
        dataset.setClassIndex(dataset.numAttributes() - 1); // Set the last attribute as the class
        return dataset;
    }

    /**
     * Processes articles concurrently, categorizing them for recommendation.
     */
    public void processArticles() {
        executorService.submit(() -> availableArticles.forEach(this::categorizeArticle));
    }

    /**
     * Shuts down the executor service used for concurrency.
     */
    public void shutDown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Error shutting down executor service: {}", e.getMessage());
            executorService.shutdownNow();
        }
    }
}
