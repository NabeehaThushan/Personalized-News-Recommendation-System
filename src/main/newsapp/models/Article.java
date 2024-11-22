package main.newsapp.models;
import java.util.Date;
import java.util.UUID;

public class Article {
    private String id;
    private String title;
    private String contentOfArticle;
    private Category category;
    private String source;
    private Date publishedDate;

    public Article(String title, String contentOfArticle, Category category, String source, Date publishedDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentOfArticle() {
        return contentOfArticle;
    }

    public void setContentOfArticle(String contentOfArticle) {
        this.contentOfArticle = contentOfArticle;
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

    public void setSource(String source) {
        this.source = source;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void categorize(){
        if (this.contentOfArticle.contains("technology")){
            this.category = Category.TECHNOLOGY;
        } else if (this.contentOfArticle.contains("health")){
            this.category = Category.HEALTH;
        } else if (this.contentOfArticle.contains("sports")) {
            this.category = Category.SPORTS;
        } else {
            this.category = Category.GENERAL;
        }
        System.out.println("Article categorized as: " + category);
    }

    @Override
    public String toString(){
        return "Article{"+ "title=" + title + ", contentOfArticle=" + contentOfArticle + ", category=" + category + ", source=" + source + ", publishedDate=" + publishedDate + '}';
    }
}
