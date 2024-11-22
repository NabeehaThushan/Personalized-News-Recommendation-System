package main.newsapp.exceptions;

// Exception: Thrown when a user tries to register but already exists
public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }
}
