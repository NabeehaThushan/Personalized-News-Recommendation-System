package main.newsapp.services;

import main.newsapp.models.Article;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.stemmer.PorterStemmer;

import main.newsapp.models.Category;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import main.newsapp.utils.FileHandler;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ArticleFetcher {
    private List<Article> articles;
    private FileHandler fileHandler;
    private ExecutorService executorService;

    public ArticleFetcher() {
        this.fileHandler = new FileHandler();
        this.articles = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(4); // Thread pool with a fixed size
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
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                System.err.println("Timeout occurred while categorizing articles.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted: " + e.getMessage());
        }
    }

    public void categorizeArticle(Article article) {
        if (article.getCategory() == null) { // If not already categorized
            String content = article.getContentOfArticle().toLowerCase();

            // Tokenize the content
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            String[] tokens = tokenizer.tokenize(content);

            // Stem the tokens
            PorterStemmer stemmer = new PorterStemmer();
            List<String> stemmedTokens = Arrays.stream(tokens)
                                               .map(stemmer::stem)
                                               .collect(Collectors.toList());

            // Match against category keywords
            for (Category category : Category.values()) {
                for (String keyword : category.getKeywords()) {
                    String stemmedKeyword = stemmer.stem(keyword.toLowerCase());
                    if (stemmedTokens.contains(stemmedKeyword)) {
                        article.setCategory(category);
                        return; // Stop checking once a match is found
                    }
                }
            }

            // Default to GENERAL if no match
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

    public void shutDownExecutor() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error shutting down executor service: " + e.getMessage());
        }
    }
}