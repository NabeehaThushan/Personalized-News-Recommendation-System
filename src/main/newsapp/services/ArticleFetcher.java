package main.newsapp.services;

import main.newsapp.models.Article;

import java.util.ArrayList;

import java.util.List;

import main.newsapp.models.Category;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import main.newsapp.utils.FileHandler;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ArticleFetcher {
    private List<Article> articles;
    private FileHandler fileHandler;
    private ExecutorService executorService;
    public ArticleFetcher() {
        this.fileHandler = new FileHandler();
        this.articles = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Get the list of articles
    public List<Article> getArticles() {
        articles = fileHandler.fetchArticlesFromFile();
        categorizeArticlesWithNLP();
        return articles;
    }


     public void categorizeArticlesWithNLP() {
       List<Runnable> tasks = new ArrayList<>();

        for (Article article : articles) {
            tasks.add(() -> categorizeArticle(article));
        }

        // Execute tasks concurrently
        tasks.forEach(executorService::submit);

        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);  // Wait for all tasks to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void categorizeArticle(Article article) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String content = article.getContentOfArticle().toLowerCase();
        String[] tokens = tokenizer.tokenize(content);

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
    // Helper method to check for any matching keywords
    public boolean containsKeywords(String[] tokens, String[] keywords) {
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
        this.articles = testArticles;
    }
}

