package springbootapp.moodle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TokenManager {

    private static String token = null;
    private static final String TOKEN_FILE = "moodle_token.txt";



    // Method to invalidate the token (if needed)
    public static void invalidateToken() {
        token = null;
        try {
            Files.deleteIfExists(Paths.get(TOKEN_FILE));  // Delete the token file if it exists
        } catch (IOException e) {
            System.err.println("Failed to delete token file: " + e.getMessage());
        }
    }

    // Load the token from the file
    public static String loadTokenFromFile() {
        Path tokenPath = Paths.get(TOKEN_FILE);
        try {
            if (Files.exists(tokenPath)) {
                // Read token from file
                return new String(Files.readAllBytes(tokenPath));
            }
        } catch (IOException e) {
            System.err.println("Failed to read token from file: " + e.getMessage());
        }
        return null;  // Return null if the token could not be loaded
    }

    // Save the token to a file
    private static void saveTokenToFile(String token) {
        Path tokenPath = Paths.get(TOKEN_FILE);
        try {
            Files.write(tokenPath, token.getBytes());
            System.out.println("Token saved to " + TOKEN_FILE);
        } catch (IOException e) {
            System.err.println("Failed to save token to file: " + e.getMessage());
        }
    }
}
