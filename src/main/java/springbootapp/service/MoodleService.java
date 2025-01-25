package springbootapp.service;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import springbootapp.model.Course;
import springbootapp.model.GradeItem;
import springbootapp.model.Student;
import springbootapp.moodle.TokenManager;

public class MoodleService {

    private static final String MOODLE_SERVICE_URL = "https://moodle.technikum-wien.at/webservice/rest/server.php";
    private final HttpClient httpClient;
    private int userId = -1;

    public MoodleService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Fetches the current user's ID by calling the Moodle API.
     */
    public int getCurrentUserId() throws IOException, InterruptedException {
        String token = TokenManager.loadTokenFromFile();

        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return -1;
        }

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
            this.userId = jsonResponse.has("userid") ? jsonResponse.get("userid").getAsInt() : -1;
            System.out.println("User ID fetched successfully: " + this.userId);
            return this.userId;
        } else {
            System.out.println("Error fetching user ID: " + response.body());
            return -1;
        }
    }

    /**
     * Retrieves the courses the user is enrolled in.
     */
    public List<Course> getUserCourses() throws IOException, InterruptedException {
        String token = TokenManager.loadTokenFromFile();
        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return new ArrayList<>(); // Leere Liste zurückgeben, um Fehler zu vermeiden
        }

        if (this.userId == -1) {
            this.userId = getCurrentUserId();
        }

        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_users_courses&moodlewsrestformat=json&userid=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Moodle API Response for Courses: " + response.body()); // Debugging-Ausgabe

        List<Course> courses = new ArrayList<>();
        try {
            // Versuchen, die Antwort als JSON-Array zu parsen
            JsonArray coursesArray = JsonParser.parseString(response.body()).getAsJsonArray();

            for (JsonElement element : coursesArray) {
                JsonObject courseJson = element.getAsJsonObject();
                int id = courseJson.get("id").getAsInt();
                String name = courseJson.get("fullname").getAsString();
                courses.add(new Course(id, name));
            }
        } catch (IllegalStateException e) {
            // Ausnahme behandeln, wenn die Antwort kein JSON-Array ist
            System.out.println("Fehler beim Abrufen der Kurse: " + response.body());
            JsonObject errorResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            if (errorResponse.has("exception")) {
                System.out.println("API-Fehler: " + errorResponse.get("message").getAsString());
            }
        }

        return courses;
    }


    /**
     * Checks if the user has the required role in a specific course.
     */
    public boolean hasRequiredRole(int courseId) throws IOException, InterruptedException {
        String token = TokenManager.loadTokenFromFile();
        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return false;
        }

        if (this.userId == -1) {
            this.userId = getCurrentUserId();
        }

        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_enrolled_users&moodlewsrestformat=json&courseid=" + courseId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonArray usersArray = JsonParser.parseString(response.body()).getAsJsonArray();

            for (JsonElement element : usersArray) {
                JsonObject user = element.getAsJsonObject();
                if (user.get("id").getAsInt() == this.userId) {
                    JsonArray rolesArray = user.getAsJsonArray("roles");
                    for (JsonElement roleElement : rolesArray) {
                        int roleId = roleElement.getAsJsonObject().get("roleid").getAsInt();
                        if (roleId == 3 || roleId == 4 || roleId == 27) { // Teacher roles
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all students and their grades for a specific course.
     */
    public List<Student> getAllStudentsWithGrades(int courseId) throws IOException, InterruptedException {
        String token = TokenManager.loadTokenFromFile();
        if (token == null) {
            System.out.println("Error: Token not found in the file");
            return new ArrayList<>();
        }

        List<Student> students = getEnrolledStudents(token, courseId);

        for (Student student : students) {
            List<GradeItem> grades = getStudentGrades(token, courseId, student.getId());
            student.setGradeItems(grades);
        }

        return students;
    }

    private List<Student> getEnrolledStudents(String token, int courseId) throws IOException, InterruptedException {
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_enrolled_users&moodlewsrestformat=json&courseid=" + courseId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<Student> students = new ArrayList<>();

        if (response.statusCode() == 200) {
            JsonArray usersArray = JsonParser.parseString(response.body()).getAsJsonArray();

            for (JsonElement element : usersArray) {
                JsonObject user = element.getAsJsonObject();
                int userId = user.has("id") && !user.get("id").isJsonNull() ? user.get("id").getAsInt() : -1;
                String fullName = user.has("fullname") && !user.get("fullname").isJsonNull()
                        ? user.get("fullname").getAsString()
                        : "Unknown";

                students.add(new Student(userId, fullName, new ArrayList<>()));
            }
        }
        return students;
    }


    private List<GradeItem> getStudentGrades(String token, int courseId, int userId) throws IOException, InterruptedException {
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=gradereport_user_get_grade_items&moodlewsrestformat=json";

        String formData = "courseid=" + URLEncoder.encode(String.valueOf(courseId), StandardCharsets.UTF_8)
                + "&userid=" + URLEncoder.encode(String.valueOf(userId), StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<GradeItem> gradeItems = new ArrayList<>();

        if (response.statusCode() == 200) {
            JsonObject responseObject = JsonParser.parseString(response.body()).getAsJsonObject();

            if (responseObject.has("usergrades")) {
                JsonArray userGradesArray = responseObject.getAsJsonArray("usergrades");

                for (JsonElement userGradeElement : userGradesArray) {
                    JsonObject userGradeObject = userGradeElement.getAsJsonObject();

                    if (userGradeObject.has("gradeitems")) {
                        JsonArray gradesArray = userGradeObject.getAsJsonArray("gradeitems");

                        for (JsonElement gradeElement : gradesArray) {
                            JsonObject gradeObj = gradeElement.getAsJsonObject();
                            int itemId = gradeObj.has("id") && !gradeObj.get("id").isJsonNull() ? gradeObj.get("id").getAsInt() : -1;

                            // Replace "Unnamed" with "Total"
                            String itemName = gradeObj.has("itemname") && !gradeObj.get("itemname").isJsonNull()
                                    ? gradeObj.get("itemname").getAsString()
                                    : "Unnamed";
                            if ("Unnamed".equals(itemName)) {
                                itemName = "Total";
                            }

                            String grade = gradeObj.has("graderaw") && !gradeObj.get("graderaw").isJsonNull()
                                    ? gradeObj.get("graderaw").getAsString()
                                    : "No Grade";

                            gradeItems.add(new GradeItem(itemId, itemName, grade));
                        }
                    }
                }
            }
        }
        return gradeItems;
    }


}