package newsapp.services;


import newsapp.models.Category;
import newsapp.models.UserPreference;
import newsapp.models.Article;
import newsapp.models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class RecommendationEngine {

    //the constructor takes in the articles as a list
    private List<Article> availableArticles;
    public RecommendationEngine(List<Article> availableArticles) {
        this.availableArticles = availableArticles;
    }


    //we trying to generate the recommendations for the given user
    public List<Article> generateRecommendations(User user){
        UserPreference preferences = user.getPreferences();
        Map<Category, Integer> categoryPreferences = preferences.getAllPreferences();
        //sort the categories by the score for preferences for a given user
        List<Category> preferredCategories = categoryPreferences.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return filterArticlesByCategory(preferredCategories);
    }
    //just a helper method to filter the articles
    private List<Article> filterArticlesByCategory(List<Category> preferredCategories) {
        List<Article> recommendedArticles = new ArrayList<>();

        for (Category category : preferredCategories) {
            List<Article> articlesForCategory = availableArticles.stream().filter(article -> article.getCategory()==category).limit(3).collect(Collectors.toList());
            recommendedArticles.addAll(articlesForCategory);
        }

        return recommendedArticles;
    }












}
