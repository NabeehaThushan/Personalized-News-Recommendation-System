package main.newsapp.models;

import main.newsapp.services.RecommendationEngine;
import main.newsapp.utils.DatabaseService;
import main.newsapp.utils.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import java.util.List;

public class Admin extends User {
    private static DatabaseService dbService;
    private static final Logger logger = LoggerFactory.getLogger(Admin.class);

    public Admin(String username, String password) {
        super(username, password);
    }

   public synchronized void addArticle(List<Article> articles, Article newArticle) {
    if (!articles.contains(newArticle)) {
        articles.add(newArticle);
        FileHandler.saveArticlesToFile(articles); // Save to file
        System.out.println("Admin added a new article: " + newArticle.getTitle());
        logger.info("Admin added a new article: {}", newArticle.getTitle());
    } else {
        System.out.println("Article already exists: " + newArticle.getTitle());
        logger.warn("Admin attempted to add an existing article: {}", newArticle.getTitle());
    }
}

public void removeArticle(List<Article> articles, String articleId) {
    boolean removed = articles.removeIf(article -> article.getId().equals(articleId));
    if (removed) {
        FileHandler.saveArticlesToFile(articles); // Save to file
        System.out.println("Admin removed article with ID: " + articleId);
    } else {
        System.out.println("No article found with ID: " + articleId);
    }
}


    public void viewUserActivity(User user) {
        System.out.println("Viewing activity for user: " + user.getUserName());
        System.out.println("Reading History:");
        for (Article article : user.getReadingHistory()) {
            System.out.println(" - " + article.getTitle() + " (" + article.getCategory() + ")");
        }
        System.out.println("User Preferences: " + user.getPreferences());
    }

    public void resetUserPreferences(User user) {
        user.setPreferences(new UserPreference());
        System.out.println("User preferences reset for: " + user.getUserName());
    }

    public void listAllArticles(List<Article> articles) {
        System.out.println("Listing all articles:");
        for (Article article : articles) {
            System.out.println(" - ID: " + article.getId() + " | Title: " + article.getTitle() + " | Category: " + article.getCategory());
        }
    }

    public void resetUserPreferences(DatabaseService dbService, String username) {
        dbService.resetUserPreferences(username);
    }

    public void clearUserReadingHistory(DatabaseService dbService, String username) {
        dbService.clearUserReadingHistory(username);
    }

}
