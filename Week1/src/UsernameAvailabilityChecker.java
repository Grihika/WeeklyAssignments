import java.util.*;

class UsernameChecker {

    private HashMap<String, Integer> users = new HashMap<>();

    private HashMap<String, Integer> attempts = new HashMap<>();

    public boolean checkAvailability(String username) {

        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    public void registerUser(String username, int userId) {
        users.put(username, userId);
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        suggestions.add(username + "1");
        suggestions.add(username + "2");
        suggestions.add(username.replace("_", "."));
        suggestions.add(username + "_official");

        return suggestions;
    }

    public String getMostAttempted() {

        String maxUser = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + maxCount + " attempts)";
    }
}

public class UsernameAvailabilityChecker {

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("john_doe", 101);
        checker.registerUser("admin", 102);
        checker.registerUser("user123", 103);

        String username = "john_doe";

        boolean available = checker.checkAvailability(username);

        if (available) {
            System.out.println("Username available: " + username);
        } else {
            System.out.println("Username taken: " + username);
            System.out.println("Suggestions: " + checker.suggestAlternatives(username));
        }

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println("Most attempted username: " + checker.getMostAttempted());
    }
}
