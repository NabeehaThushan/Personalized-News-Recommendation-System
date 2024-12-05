package main.newsapp.utils;

import main.newsapp.models.Article;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import main.newsapp.models.Category;
import org.json.JSONArray;
import org.json.JSONObject;

public class FileHandler {
    private static final String FILE_PATH = "src/articles.json";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(FileHandler.class);

    public List<Article> fetchArticlesFromFile() {
        List<Article> articles = new ArrayList<>();
        try {
            logger.info("Fetching articles from file: {}", FILE_PATH);
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
            if (content == null || content.trim().isEmpty()) {
                logger.warn("File is empty: {}", FILE_PATH);
                return articles;
            }

            JSONArray articlesArray = new JSONArray(content);
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject articleJson = articlesArray.getJSONObject(i);
                String title = articleJson.getString("title");
                String description = articleJson.optString("description", "No description available");
                String contentOfArticle = articleJson.getString("contentOfArticle");
                String source = articleJson.optString("source", "Unknown Source");
                String dateString = articleJson.optString("publishedDate", "2022-01-01");
                Date publishedDate = parseDate(dateString);
                Category category = Category.GENERAL;

                // Parse category if available
                String categoryString = articleJson.optString("category", "GENERAL");
                try {
                    category = Category.valueOf(categoryString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid category '{}' in file, defaulting to GENERAL.", categoryString);
                    category = Category.GENERAL; // Fallback for unknown categories
                }

                Article article = new Article(title,description, contentOfArticle, category, source, publishedDate);
                articles.add(article);
            }
            logger.info("Successfully fetched {} articles from file.", articles.size());
        } catch (IOException e) {
            logger.error("Error reading or parsing the file: {}", FILE_PATH, e);

        }
        return articles;
    }

    public synchronized static void saveArticlesToFile(List<Article> articles) {
        JSONArray articlesArray = new JSONArray();
        for (Article article : articles) {
            JSONObject articleJson = new JSONObject();
            articleJson.put("title", article.getTitle());
            articleJson.put("description", article.getDescription());
            articleJson.put("contentOfArticle", article.getContentOfArticle());
            articleJson.put("source", article.getSource());
            articleJson.put("publishedDate", DATE_FORMAT.format(article.getPublishedDate()));
            articleJson.put("category", article.getCategory().name());
            articlesArray.put(articleJson);
        }

        try {
            Files.write(Paths.get(FILE_PATH), articlesArray.toString(4).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            logger.info("Articles successfully saved to file: {}", FILE_PATH);
            System.out.println("Articles saved to file successfully.");
        } catch (IOException e) {
            logger.error("Error saving articles to file: {}", FILE_PATH, e);
            System.err.println("Error saving articles to file: " + FILE_PATH);
            e.printStackTrace();
        }
    }

     public static void backupArticlesFile() {
    String backupFilePath = "src/articles_backup.json";
    try {
        Files.copy(Paths.get(FILE_PATH), Paths.get(backupFilePath));
        logger.info("Backup created successfully: {}", backupFilePath);
    } catch (IOException e) {
        logger.error("Error creating backup file: {}", backupFilePath, e);
    }
}


    public Date parseDate(String dateString) {
        try {
            if (dateString == null || dateString.trim().isEmpty()) {
                System.err.println("Invalid or empty date string. Using default date.");
                logger.warn("Invalid or empty date string. Using default date.");
                return DATE_FORMAT.parse("1970-01-01"); // Default fallback date
            }
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            logger.warn("Error parsing date: {}. Using default date.", dateString);
            System.err.println("Error parsing date: " + dateString + ". Using default date.");
            try {
                return DATE_FORMAT.parse("1970-01-01");
            } catch (ParseException ignored) {
                return null; // Should never happen
            }
        }
    }


}
