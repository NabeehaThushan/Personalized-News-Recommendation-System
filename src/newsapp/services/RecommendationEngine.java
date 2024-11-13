package newsapp.services;


import newsapp.models.Category;
import newsapp.models.UserPreference;
import newsapp.models.Article;
import newsapp.models.User;
import newsapp.utils.DatabaseHandler;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.stream.Collectors;


public class RecommendationEngine {

    //the constructor takes in the articles as a list
    private List<Article> availableArticles;
    private DatabaseHandler databaseHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public RecommendationEngine(List<Article> availableArticles, DatabaseHandler databaseHandler) {
        this.availableArticles = availableArticles;
        this.databaseHandler = databaseHandler;
    }

        //we trying to generate the recommendations for the given user
        public List<Article> generateRecommendations (User user){
            databaseHandler.loadPreferences(user);
            updateUserPreference(user);
            List<Article> recommendations = new ArrayList<>();

            try {
                CompletableFuture<Map<Category, Integer>> categoryPreferencesFuture = CompletableFuture.supplyAsync(() -> user.getPreferences().getAllPreferences(), executorService);

                Map<Category, Integer> sortedPreferences = categoryPreferencesFuture.get().entrySet().stream().sorted(Map.Entry.<Category, Integer>comparingByValue().reversed())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                CompletableFuture<?>[] recommendationFutures = sortedPreferences.keySet().stream().map(category -> CompletableFuture.runAsync(
                        () -> recommendations.addAll(getArticlesByCategory(category, 3)), executorService
                )).toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(recommendationFutures).join();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return recommendations;

        }

        public void updateUserPreference (User user){
            Map<Category, Integer> preferences = user.getPreferences().getAllPreferences();

            for (Article article : user.getReadingHistory()) {
                Category category = analyzeSentimentAndCategorize(article.getContentOfArticle());
                preferences.merge(category, 1, Integer::sum);

            }

            user.getPreferences().setAllPreferences(preferences);
            databaseHandler.storePreferences(user);
        }

        private Category analyzeSentimentAndCategorize (String contentOfArticle){
            if (contentOfArticle.contains("technology")) return Category.TECHNOLOGY;
            else if (contentOfArticle.contains("health")) return Category.HEALTH;
            else if (contentOfArticle.contains("sports")) return Category.SPORTS;
            else return Category.GENERAL;
        }


        private List<Article> getArticlesByCategory (Category category,int limit){
            return availableArticles.parallelStream().filter(article -> article.getCategory().equals(category))
                    .limit(limit).collect(Collectors.toList());
        }


        public void shutDown () {
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
