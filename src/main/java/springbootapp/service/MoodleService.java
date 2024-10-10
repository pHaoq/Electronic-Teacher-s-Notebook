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

public class MoodleService {

    private static final String MOODLE_SERVICE_URL = "https://moodle.technikum-wien.at/webservice/rest/server.php";
    private final HttpClient httpClient;

    // Constructor to initialize HttpClient
    public MoodleService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public int getCurrentUserId(String token) throws IOException, InterruptedException {
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

    public List<JsonObject> getUserCourses(String token, int userId) throws IOException, InterruptedException {
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
            List<JsonObject> courses = new ArrayList<>();
            for (int i = 0; i < coursesArray.size(); i++) {
                courses.add(coursesArray.get(i).getAsJsonObject());
            }
            return courses;
        } else {
            parseAndLogError(response.body());
            return null;
        }
    }

    private void parseAndLogError(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String error = jsonObject.has("error") ? jsonObject.get("error").getAsString() : "Unknown error";
        System.out.println("Error: " + error);
    }
}
