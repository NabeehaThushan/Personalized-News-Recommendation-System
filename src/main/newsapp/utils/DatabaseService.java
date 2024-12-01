package main.newsapp.utils;

import main.newsapp.exceptions.DuplicateUserException;
import main.newsapp.exceptions.UserNotFoundException;
import main.newsapp.exceptions.IncorrectPasswordException;
import main.newsapp.models.Article;
import main.newsapp.models.User;

import java.util.List;
import java.util.Map;

public interface DatabaseService {
    /**
     * Checks if a user exists in the database.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(String username);

    /**
     * Validates a user's credentials.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the credentials are valid
     * @throws UserNotFoundException if the user does not exist
     * @throws IncorrectPasswordException if the password is incorrect
     */
    boolean validateUser(String username, String password) throws UserNotFoundException, IncorrectPasswordException;

    /**
     * Registers a new user in the database.
     *
     * @param username the username of the user to register
     * @param password the password of the user
     * @throws DuplicateUserException if the username already exists
     */
    void registerUser(String username, String password) throws DuplicateUserException;

    /**
     * Stores user preferences in the database.
     *
     * @param user the user whose preferences are to be stored
     */
    void storePreferences(User user);

    /**
     * Loads user preferences from the database.
     *
     * @param user the user whose preferences are to be loaded
     */
    void loadPreferences(User user);

    /**
 * Stores an article rating in the database.
 *
 * @param username the username of the user rating the article
 * @param articleId the ID of the article being rated
 * @param rating the rating given by the user
 */
    void storeArticleRating(String username, String articleId, int rating);
 Map<String, Integer> getUserRatings(String username);

  /**
     * Stores user reading history in the database.
     *
     * @param user the user whose reading history is to be stored
     */
    void storeReadingHistory(User user);

    /**
     * Loads user reading history from the database.
     *
     * @param user the user whose reading history is to be loaded
     * @param allArticles the list of all available articles
     */
    void loadReadingHistory(User user, List<Article> allArticles);

    void resetUserPreferences(String username);

    void clearUserReadingHistory(String username);

    /**
     * Stores a user's rating for an article in the database.
     *
     * @param username the username of the user
     * @param articleId the ID of the article
     * @param rating the rating given to the article
     */







}


