package main.newsapp.utils;

import main.newsapp.models.Article;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.newsapp.models.Category;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileHandler {
    private static final String FILE_PATH = "articles.json";
    private List<Article> articles;


    public List<Article> fetchArticlesFromFile() {
           List<Article> articles = new ArrayList<>();
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

}
