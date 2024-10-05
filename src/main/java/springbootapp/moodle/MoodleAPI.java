package springbootapp.moodle;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MoodleAPI {

    private static final String MOODLE_API_URL = "https://moodle.technikum-wien.at/login/token.php";
    private static final String MOODLE_SERVICE_URL = "https://moodle.technikum-wien.at/webservice/rest/server.php";
    private static final String SERVICE = "moodle_mobile_app";

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
    public String authenticate(String username, String password) throws IOException, InterruptedException {
        // Prepare the form data with URL encoding
        String form = buildFormData(username, password, SERVICE);

        // Build the HTTP POST request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MOODLE_API_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Log the response details
        System.out.println("HTTP Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Handle the response
        if (response.statusCode() == 200) {
            String token = parseTokenFromResponse(response.body());
            if (token != null) {
                System.out.println("Token successfully retrieved: " + token);
                return token;
            } else {
                System.out.println("Token was not found in the response. Check Moodle service or response structure.");
                return null;
            }
        } else {
            // Handle non-200 responses
            System.out.println("Error: Received HTTP response code " + response.statusCode());
            parseAndLogError(response.body());
            return null;
        }
    }

    /**
     * Retrieve the current user's ID and site information.
     *
     * @param token Authentication token
     * @return The user ID of the current user
     * @throws IOException          If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public int getCurrentUserId(String token) throws IOException, InterruptedException {
        // Build the URL to fetch the site info (which includes the user ID)
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json";

        // Build the HTTP GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Handle the response
        if (response.statusCode() == 200) {
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            if (jsonResponse.has("userid")) {
                int userId = jsonResponse.get("userid").getAsInt();
                System.out.println("Current user ID: " + userId);
                return userId;
            } else {
                System.out.println("Failed to retrieve the user ID from the response.");
                return -1;
            }
        } else {
            // Handle non-200 responses
            System.out.println("Error: Received HTTP response code " + response.statusCode());
            parseAndLogError(response.body());
            return -1;
        }
    }

    /**
     * Retrieve the list of courses for the user.
     *
     * @param token The authentication token
     * @param userId The ID of the user
     * @return List of courses in JSON format
     * @throws IOException          If an I/O error occurs
     * @throws InterruptedException If the operation is interrupted
     */
    public List<JsonObject> getUserCourses(String token, int userId) throws IOException, InterruptedException {
        // Build the request URL for fetching courses
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_users_courses&moodlewsrestformat=json&userid=" + userId;

        // Build the HTTP GET request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        // Send the request and get the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Log the response details
        System.out.println("HTTP Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Handle the response
        if (response.statusCode() == 200) {
            JsonArray coursesArray = JsonParser.parseString(response.body()).getAsJsonArray();
            // Convert the JsonArray to a List of JsonObjects (representing each course)
            List<JsonObject> courses = new ArrayList<>();
            for (int i = 0; i < coursesArray.size(); i++) {
                courses.add(coursesArray.get(i).getAsJsonObject());
            }
            return courses;
        } else {
            // Handle non-200 responses
            System.out.println("Error: Received HTTP response code " + response.statusCode());
            parseAndLogError(response.body());
            return null;
        }
    }

    /**
     * Build URL-encoded form data.
     *
     * @param username Moodle username
     * @param password Moodle password
     * @param service  Moodle service name
     * @return URL-encoded form data string
     */
    private String buildFormData(String username, String password, String service) {
        return "username=" + urlEncode(username) +
                "&password=" + urlEncode(password) +
                "&service=" + urlEncode(service);
    }

    /**
     * URL-encode a string using UTF-8 encoding.
     *
     * @param value The string to encode
     * @return URL-encoded string
     */
    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Parse the authentication token from the JSON response.
     *
     * @param jsonResponse JSON response from Moodle API
     * @return Authentication token if present, null otherwise
     */
    private String parseTokenFromResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (jsonObject.has("token")) {
                return jsonObject.get("token").getAsString();
            } else if (jsonObject.has("error")) {
                // Handle specific error messages if needed
                String error = jsonObject.get("error").getAsString();
                System.out.println("Authentication Error: " + error);
            }
        } catch (Exception e) {
            System.out.println("Failed to parse JSON response: " + e.getMessage());
        }
        return null;
    }

    /**
     * Parse and log error details from the JSON response.
     *
     * @param jsonResponse JSON error response from Moodle API
     */
    private void parseAndLogError(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            if (jsonObject.has("error")) {
                String error = jsonObject.get("error").getAsString();
                String errorCode = jsonObject.has("errorcode") ? jsonObject.get("errorcode").getAsString() : "N/A";
                System.out.println("Error Details:");
                System.out.println("Error Code: " + errorCode);
                System.out.println("Error Message: " + error);
            } else {
                System.out.println("Unexpected error response format.");
            }
        } catch (Exception e) {
            System.out.println("Failed to parse error JSON response: " + e.getMessage());
        }
    }
}
