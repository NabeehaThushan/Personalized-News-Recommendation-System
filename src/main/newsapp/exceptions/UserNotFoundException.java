package main.newsapp.exceptions;

// Exception: Thrown when a user tries to log in but doesn't exist in the database
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }
}
