package springbootapp.service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import springbootapp.model.GradeItem;
import springbootapp.model.Student;
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
     * Get a list of the user's enrolled courses where they are a tutor (teacher) by calling Moodle's API.
     *
     * @return list of courses where the user is a tutor, or null if there's an error
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
                // Optionally, fetch role (if available)
                String role = courseJson.has("role") ? courseJson.get("role").getAsString() : null;

                // Create Course object and add to the list, passing role if available
                courses.add(new Course(courseId, courseName, role));
            }
            return courses;
        } else {
            parseAndLogError(response.body());
            return null;
        }
    }

    /**
     * Method to retrieve all students and their grades for a given course.
     *
     * @param courseId The course ID.
     * @return A list of Student objects with their grades.
     * @throws IOException, InterruptedException
     */
    public List<Student> getAllStudentsWithGrades(int courseId) throws IOException, InterruptedException {
        String token = TokenManager.loadTokenFromFile();

        // Step 1: Get all enrolled user IDs
        List<Student> students = getEnrolledStudents(token, courseId);

        // Step 2: For each student, retrieve their grades
        for (Student student : students) {
            List<GradeItem> grades = getStudentGrades(token, courseId, student.getId());
            student.setGradeItems(grades);  // Set grades for the student
        }

        return students;
    }

    /**
     * Helper method to get all enrolled students in a course.
     *
     * @param token    Moodle web service token
     * @param courseId The course ID
     * @return A list of Student objects
     * @throws IOException, InterruptedException
     */
    private List<Student> getEnrolledStudents(String token, int courseId) throws IOException, InterruptedException {
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=core_enrol_get_enrolled_users&moodlewsrestformat=json&courseid=" + courseId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Print the full JSON response
        System.out.println("JSON Response for enrolled students: " + response.body());

        if (response.statusCode() == 200) {
            JsonArray usersArray = JsonParser.parseString(response.body()).getAsJsonArray();
            List<Student> students = new ArrayList<>();

            for (JsonElement element : usersArray) {
                JsonObject user = element.getAsJsonObject();
                int userId = user.get("id").getAsInt();
                String fullName = user.get("fullname").getAsString();

                // Extract roles as an array
                List<Integer> roles = new ArrayList<>();
                if (user.has("roles") && user.get("roles").isJsonArray()) {
                    JsonArray rolesArray = user.getAsJsonArray("roles");
                    for (JsonElement roleElement : rolesArray) {
                        JsonObject roleObj = roleElement.getAsJsonObject();
                        Integer roleID = roleObj.get("roleid").getAsInt();
                        roles.add(roleID);  // Add the role name to the list
                    }
                }

                // Create a new Student object with the roles
                Student student = new Student(userId, fullName, roles);
                students.add(student);
            }
            return students;
        } else {
            System.out.println("Failed to fetch enrolled students.");
            return new ArrayList<>();
        }
    }


    /**
     * Helper method to get grades for a single student in a course.
     *
     * @param token    Moodle web service token
     * @param courseId The course ID
     * @param userId   The user ID
     * @return A list of GradeItem objects for the student
     * @throws IOException, InterruptedException
     */
    private List<GradeItem> getStudentGrades(String token, int courseId, int userId) throws IOException, InterruptedException {
        String url = MOODLE_SERVICE_URL + "?wstoken=" + token
                + "&wsfunction=gradereport_user_get_grade_items&moodlewsrestformat=json";

        // Build the POST data
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

            // First, check if "usergrades" array exists
            if (responseObject.has("usergrades") && !responseObject.get("usergrades").isJsonNull()) {
                JsonArray userGradesArray = responseObject.getAsJsonArray("usergrades");

                // Loop through the "usergrades" array
                for (JsonElement userGradeElement : userGradesArray) {
                    JsonObject userGradeObject = userGradeElement.getAsJsonObject();

                    // Check if "gradeitems" array exists for each user grade
                    if (userGradeObject.has("gradeitems") && !userGradeObject.get("gradeitems").isJsonNull()) {
                        JsonArray gradesArray = userGradeObject.getAsJsonArray("gradeitems");

                        // Now loop through the "gradeitems" array
                        if (gradesArray != null && gradesArray.size() > 0) {
                            for (JsonElement gradeElement : gradesArray) {
                                JsonObject gradeObj = gradeElement.getAsJsonObject();
                                int itemId = gradeObj.get("id").getAsInt();
                                String itemName = gradeObj.has("itemname") && !gradeObj.get("itemname").isJsonNull()
                                        ? gradeObj.get("itemname").getAsString() : "Unnamed";
                                String grade = gradeObj.has("graderaw") && !gradeObj.get("graderaw").isJsonNull()
                                        ? gradeObj.get("graderaw").getAsString() : "No Grade";

                                // Create a new GradeItem object
                                GradeItem gradeItem = new GradeItem(itemId, itemName, grade);
                                gradeItems.add(gradeItem);
                            }
                        } else {
                            System.out.println("No grades found in 'gradeitems' for user ID: " + userId);
                        }
                    } else {
                        System.out.println("No 'gradeitems' found for user ID: " + userId);
                    }
                }
            } else {
                System.out.println("No 'usergrades' found in response for user ID: " + userId);
            }
        } else {
            System.out.println("Failed to fetch grades for user ID " + userId + ". Response: " + response.body());
        }

        return gradeItems;
    }



    // Helper method to log errors
    private void parseAndLogError(String jsonResponse) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        String error = jsonObject.has("error") ? jsonObject.get("error").getAsString() : "Unknown error";
        System.out.println("Error: " + error);
    }
}
