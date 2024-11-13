package newsapp.services;

import newsapp.models.Article;

import java.util.ArrayList;

import java.util.List;

import newsapp.models.Category;

import newsapp.utils.FileHandler;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ArticleFetcher {
    private List<Article> articles;
    private FileHandler fileHandler;

    public ArticleFetcher() {
        this.fileHandler = new FileHandler();
        this.articles = new ArrayList<>();
    }

    // Get the list of articles
    public List<Article> getArticles() {
        return fileHandler.fetchArticlesFromFile();
    }


     public void categorizeArticlesWithNLP() {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

        for (Article article : articles) {
            String content = article.getContentOfArticle().toLowerCase();
            String[] tokens = tokenizer.tokenize(content);

            // Check keywords for each category
            if (containsKeywords(tokens, new String[]{"technology", "ai", "machine learning"})) {
                article.setCategory(Category.TECHNOLOGY);
            } else if (containsKeywords(tokens, new String[]{"health", "wellness", "mental"})) {
                article.setCategory(Category.HEALTH);
            } else if (containsKeywords(tokens, new String[]{"sports", "football", "basketball"})) {
                article.setCategory(Category.SPORTS);
            } else if (containsKeywords(tokens, new String[]{"quantum", "physics", "science"})) {
                article.setCategory(Category.SCIENCE);
            } else {
                article.setCategory(Category.GENERAL);
            }
        }
    }

    // Helper method to check for any matching keywords
    private boolean containsKeywords(String[] tokens, String[] keywords) {
        for (String token : tokens) {
            for (String keyword : keywords) {
                if (token.equals(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setArticles(List<Article> testArticles) {

    }
}



