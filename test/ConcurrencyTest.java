import main.newsapp.models.Category;
import main.newsapp.models.User;
import main.newsapp.utils.DatabaseHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrencyTest {
    public static void main(String[] args) {
        int numberOfUsers = 5; // Simulating 5 users
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);

        for (int i = 1; i <= numberOfUsers; i++) {
            int userId = i;
            executorService.submit(() -> simulateUserActions(userId));
        }

        executorService.shutdown();
    }

    private static void simulateUserActions(int userId) {
        String username = "User" + userId;
        String password = "password" + userId;

        try {
            User user = new User(username, password);
            DatabaseHandler dbHandler = new DatabaseHandler();

            // Register user
            if (!dbHandler.userExists(username)) {
                user.register(dbHandler);
                System.out.println(username + " registered successfully.");
            }

            // Log in user
            if (user.login(dbHandler)) {
                System.out.println(username + " logged in successfully.");
                simulateArticleViewing(user);
                simulatePreferenceUpdate(user);
            }
        } catch (Exception e) {
            System.err.println("Error for " + username + ": " + e.getMessage());
        }
    }

    private static void simulateArticleViewing(User user) throws InterruptedException {
        System.out.println(user.getUserName() + " is viewing articles...");
        Thread.sleep(1000); // Simulate time taken to view articles
    }

    private static void simulatePreferenceUpdate(User user) {
        System.out.println(user.getUserName() + " is updating preferences...");
        user.getPreferences().setPreference(Category.TECHNOLOGY, 5);
        System.out.println(user.getUserName() + " updated preferences.");
    }
}
