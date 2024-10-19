package springbootapp.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import springbootapp.moodle.TokenManager;
import springbootapp.model.Course; // Import the Course model

public class MoodleService {

    private static final String MOODLE_SERVICE_URL = "https://moodle.technikum-wien.at/webservice/rest/server.php";
    private final HttpClient httpClient;

    // Constructor to initialize HttpClient
    public MoodleService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Get the current user's ID by calling Moodle's API.
     *
     * @return the user ID, or -1 if there's an error
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public int getCurrentUserId() throws IOException, InterruptedException {
        // Fetch the token using TokenManager's loadTokenFromFile method
        String token = TokenManager.loadTokenFromFile();

        // If token is null, return -1 (indicates error)
        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return -1;
        }

        // Build URL for the current user ID request
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            return jsonResponse.has("userid") ? jsonResponse.get("userid").getAsInt() : -1;
        } else {
            parseAndLogError(response.body());
            return -1;
        }
    }

    /**
     * Get a list of the user's enrolled courses by calling Moodle's API.
     *
     * @return list of courses the user is enrolled in, or null if there's an error
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<Course> getUserCourses() throws IOException, InterruptedException {
        // Fetch the token using TokenManager's loadTokenFromFile method
        String token = TokenManager.loadTokenFromFile();

        // If token is null, return null (indicates error)
        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return null;
        }

        // Fetch the current user's ID first
        int userId = getCurrentUserId();
        if (userId == -1) {
            return null;
        }

        // Build URL for the user courses request
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_users_courses&moodlewsrestformat=json&userid=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonArray coursesArray = JsonParser.parseString(response.body()).getAsJsonArray();
            List<Course> courses = new ArrayList<>();
            for (int i = 0; i < coursesArray.size(); i++) {
                JsonObject courseJson = coursesArray.get(i).getAsJsonObject();
                int courseId = courseJson.get("id").getAsInt();
                String courseName = courseJson.get("fullname").getAsString();
                // Create Course object and add to the list
                courses.add(new Course(courseId, courseName));
            }
            return courses;
        } else {
            parseAndLogError(response.body());
            return null;
        }
    }

    // Helper method to log errors
    private void parseAndLogError(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String error = jsonObject.has("error") ? jsonObject.get("error").getAsString() : "Unknown error";
        System.out.println("Error: " + error);
    }
}