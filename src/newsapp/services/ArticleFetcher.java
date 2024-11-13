package newsapp.services;

import newsapp.models.Article;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import newsapp.models.Category;
import org.json.JSONArray;
import org.json.JSONObject;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ArticleFetcher {

    private List<Article> articles;
    private static final String FILE_PATH = "";


    public ArticleFetcher() {
        this.articles = new ArrayList<>();
    }

    public List<Article> fetchArticlesFromFile() {
        try{
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            JSONArray articlesArray = new JSONArray(content);

            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleJson = articlesArray.getJSONObject(i);
                String title = articleJson.getString("title");
                String contentOfArticle = articleJson.optString("description","No description available");
                String source = articleJson.optString("source", "Unknown Source");
                String dateString = articleJson.optString("publishedDate", "2022-01-01");
                Date publishedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                Category category = Category.GENERAL;

                Article article = new Article(title, contentOfArticle, category, source, publishedDate);
                this.articles.add(article);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return this.articles;
    }



    // Get the list of articles
    public List<Article> getArticles() {
        return this.articles;
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
}



