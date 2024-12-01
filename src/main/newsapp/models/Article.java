package main.newsapp.models;
import java.util.Date;
import java.util.UUID;

public class Article {
    private String id;
    private String title;
    private String description;
    private String contentOfArticle;
    private Category category;
    private String source;
    private Date publishedDate;

    public Article(String title, String description, String contentOfArticle, Category category, String source, Date publishedDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.contentOfArticle = contentOfArticle;
        this.category = category;
        this.source = source;
        this.publishedDate = publishedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentOfArticle() {
        return contentOfArticle;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void categorizeArticle(Article article) {
    if (article.getCategory() == null) { // If not already categorized
        String content = article.getContentOfArticle().toLowerCase();

        for (Category category : Category.values()) {
            if (category.getKeywords().stream().anyMatch(content::contains)) {
                article.setCategory(category);
                return; // Stop checking once a match is found
            }
        }

        // Assign General category if no match
        article.setCategory(Category.GENERAL);
    }
}



    @Override
    public String toString() {
        return String.format("Article{id='%s', title='%s', category=%s, source='%s', publishedDate=%s}",
                id, title, category, source, publishedDate);
    }
}
