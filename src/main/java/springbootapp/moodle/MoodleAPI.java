package springbootapp.moodle;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MoodleAPI {

    private static final String MOODLE_API_URL = "https://moodle.technikum-wien.at/login/token.php";
    private final HttpClient httpClient;

    // Constructor to initialize HttpClient
    public MoodleAPI() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Authenticate with Moodle API and retrieve a token.
     *
     * @param username Moodle username
     * @param password Moodle password
     * @return Authentication token if successful, null otherwise
     * @throws IOException          If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public boolean authenticate(String username, String password) throws IOException, InterruptedException {
        String form = buildFormData(username, password, "moodle_mobile_app");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MOODLE_API_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Save the response to file for debugging or logging
        saveResponseToFile(response.body(), "response.json");

        // If the status code is 200, try to parse the token
        if (response.statusCode() == 200) {
            String token = parseTokenFromResponse(response.body());

            // If token is valid, save it to a file and return true
            if (token != null) {
                saveTokenToFile(token);
                return true;  // Authentication successful
            }
        }

        // If we get here, either response code was not 200 or token was invalid
        parseAndLogError(response.body());
        return false;  // Authentication failed
    }


    private void saveTokenToFile(String token) {
        Path tokenFilePath = Paths.get("moodle_token.txt");
        try {
            Files.write(tokenFilePath, token.getBytes(StandardCharsets.UTF_8));
            System.out.println("Token saved successfully to moodle_token.txt");
        } catch (IOException e) {
            System.err.println("Error saving token to file: " + e.getMessage());
        }
    }



    // Helper methods remain unchanged...

    private String buildFormData(String username, String password, String service) {
        return "username=" + urlEncode(username) +
                "&password=" + urlEncode(password) +
                "&service=" + urlEncode(service);
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String parseTokenFromResponse(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return jsonObject.has("token") ? jsonObject.get("token").getAsString() : null;
    }

    private void parseAndLogError(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String error = jsonObject.has("error") ? jsonObject.get("error").getAsString() : "Unknown error";
        System.out.println("Error: " + error);
    }

    private void saveResponseToFile(String jsonResponse, String filename) {
        try (FileWriter file = new FileWriter(filename)) {
            file.write(jsonResponse);
            System.out.println("Response saved to " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save response to file: " + e.getMessage());
        }
    }
}
