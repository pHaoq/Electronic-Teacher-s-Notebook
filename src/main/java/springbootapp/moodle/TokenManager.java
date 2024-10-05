package springbootapp.moodle;

import java.io.IOException;

public class TokenManager {

    // Token is stored in memory
    private static String token = null;

    // Method to retrieve the current token or request a new one if not available
    // Now also throws InterruptedException
    public static String getToken(String username, String password) throws IOException, InterruptedException {
        if (token == null) {
            // Get a new token from the Moodle API
            MoodleAPI moodleAPI = new MoodleAPI();
            token = moodleAPI.authenticate(username, password);
        }
        return token;
    }

    // Method to invalidate the token (if needed)
    public static void invalidateToken() {
        token = null;
    }
}
