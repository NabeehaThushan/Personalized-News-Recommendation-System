package main.newsapp.services;

import main.newsapp.models.Article;
import main.newsapp.models.Category;
import main.newsapp.models.User;
import main.newsapp.utils.DatabaseHandler;

import opennlp.tools.tokenize.SimpleTokenizer;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.*;


import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RecommendationEngine {
    private List<Article> availableArticles;
    private DatabaseHandler databaseHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private Classifier mlModel;

    public RecommendationEngine(List<Article> availableArticles, DatabaseHandler databaseHandler) {
        this.availableArticles = availableArticles;
        this.databaseHandler = databaseHandler;
        loadMLModel();
    }

    // Load a pre-trained ML model
    private void loadMLModel() {
        try {
            mlModel = (Classifier) weka.core.SerializationHelper.read("src/main/newsapp/resources/ml-model.model");
        } catch (Exception e) {
            System.out.println("Error loading ML model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Generate recommendations for a user
    public List<Article> generateRecommendations(User user) {
        databaseHandler.loadPreferences(user);
        updateUserPreference(user);
        List<Article> recommendations = new ArrayList<>();

        try {
            CompletableFuture<Map<Category, Integer>> categoryPreferencesFuture =
                    CompletableFuture.supplyAsync(() -> user.getPreferences().getAllPreferences(), executorService);

            Map<Category, Integer> sortedPreferences = categoryPreferencesFuture.get().entrySet().stream()
                    .sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            CompletableFuture<?>[] recommendationFutures = sortedPreferences.keySet().stream().map(category ->
                    CompletableFuture.runAsync(() -> recommendations.addAll(getArticlesByCategory(category, 3)), executorService)
            ).toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(recommendationFutures).join();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recommendations.stream().distinct().collect(Collectors.toList());
    }

    // Update user preferences based on reading history
    public void updateUserPreference(User user) {
        try {
            Map<Category, Integer> preferences = user.getPreferences().getAllPreferences();

            for (Article article : user.getReadingHistory()) {
                Category category = categorizeWithNLP(article.getContentOfArticle());
                preferences.merge(category, 1, Integer::sum);
            }

            user.getPreferences().setAllPreferences(preferences);
            databaseHandler.storePreferences(user);
        } catch (Exception e) {
            System.out.println("Error updating user preferences: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Categorize articles using NLP
    private Category categorizeWithNLP(String content) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(content.toLowerCase());

        if (Arrays.asList(tokens).contains("technology")) return Category.TECHNOLOGY;
        if (Arrays.asList(tokens).contains("health")) return Category.HEALTH;
        if (Arrays.asList(tokens).contains("sports")) return Category.SPORTS;
        return Category.GENERAL;
    }

    // Get articles by category with an ML model for prioritization
    private List<Article> getArticlesByCategory(Category category, int limit) {
        return availableArticles.parallelStream()
                .filter(article -> article.getCategory() == category)
                .sorted((a1, a2) -> predictArticlePriority(a2) - predictArticlePriority(a1))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Predict article priority using ML
    public int predictArticlePriority(Article article) {
    try {
        // Load the ARFF dataset
        File arffFile = new File("src/main/newsapp/resources/article-data.arff");
        Instances dataset = new Instances(new FileReader(arffFile));
        dataset.setClassIndex(dataset.numAttributes() - 1); // Set the last attribute as the class

        // Create a new instance and add it to the dataset
        Instance instance = new DenseInstance(dataset.numAttributes());
        instance.setValue(dataset.attribute("length"), article.getContentOfArticle().length());
        instance.setValue(dataset.attribute("category"), article.getCategory().name());
        dataset.add(instance); // Add the instance to the dataset

        // Use the model to classify the instance
        double priority = mlModel.classifyInstance(dataset.lastInstance());
        return (int) priority; // Convert the predicted value to an integer
    } catch (Exception e) {
        System.err.println("Error in predictArticlePriority: " + e.getMessage());
        e.printStackTrace();
        return 0; // Default priority if classification fails
    }
}

    // Get articles based on user's preferences (Content-based filtering)
    private List<Article> getArticlesByUserPreferences(Map<Category, Integer> sortedPreferences) {
        List<Article> recommendations = new ArrayList<>();

        for (Map.Entry<Category, Integer> entry : sortedPreferences.entrySet()) {
            Category preferredCategory = entry.getKey();
            List<Article> filteredArticles = availableArticles.stream()
                    .filter(article -> article.getCategory() == preferredCategory)
                    .collect(Collectors.toList());

            recommendations.addAll(filteredArticles);
        }

        return recommendations;
    }

    // Get articles based on KNN (Collaborative Filtering)
    private List<Article> getArticlesByKNN(User user) {
        List<Article> recommendations = new ArrayList<>();

        // Implement KNN here (or use a pre-trained model)
        // For now, we'll just recommend articles from the top preferences
        Category topCategory = user.getPreferences().getTopPreference();

        List<Article> knnRecommendations = availableArticles.stream()
                .filter(article -> article.getCategory() == topCategory)
                .collect(Collectors.toList());

        recommendations.addAll(knnRecommendations);
        return recommendations;
    }

    public void shutDown() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        }
    }
}
