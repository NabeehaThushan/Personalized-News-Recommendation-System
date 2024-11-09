package newsapp.models;

import java.util.List;

public class Admin extends User {
    public Admin(String username, String password) {
        super(username, password);
    }

    public void addArticle(List<Article> articles, Article newArticle) {
        if (!articles.contains(newArticle)) {
            articles.add(newArticle);
            System.out.println("Admin added a new article: " + newArticle.getTitle());
        } else {
            System.out.println("Article already exists: " + newArticle.getTitle());
        }
    }

    public void removeArticle(List<Article> articles, String articleId){
        boolean removed = articles.removeIf(article -> article.getId().equals(articleId));
        if (removed) {
            System.out.println("Admin removed article with ID: " + articleId);
        } else {
            System.out.println("No article found with  ID " + articleId);
        }
    }

    public void viewUserActivity(User user){
        System.out.println("Viewing activity for user: " + user.getUserName());
        System.out.println("Reading History");
        for (Article article : user.getReadingHistory()){
            System.out.println(" - " + article.getTitle() + " (" + article.getCategory() + ")" );
        }
        System.out.println("User Preferences: " + user.getPreferences().getAllPreferences());

    }

    public void resetUserPreferences(User user){
        user.setPreferences(new UserPreference());
        System.out.println("User preference reset for: " + user.getUserName());
    }

    public void listAllArticles(List<Article> articles){
        System.out.println("Listing all articles");
        for (Article article : articles){
            System.out.println(" - ID: " + article.getId() + " | Title: " + article.getTitle() + " | Category: " + article.getCategory());
        }
    }
}
