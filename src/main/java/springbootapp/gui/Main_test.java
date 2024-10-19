package springbootapp.gui;

import springbootapp.moodle.TokenManager;
import springbootapp.service.MoodleService;
import springbootapp.model.Course;
import java.io.IOException;
import java.util.List;

public class Main_test {
    public static void main(String[] args) {
        // Credentials for Moodle login
        String username = "wi22b011";
        String password = ;

        try {
            // Fetch token using TokenManager (either from file or by requesting a new one)
            String token = TokenManager.getToken(username, password);

            if (token != null) {
                System.out.println("Token successfully retrieved: " + token);

                // Initialize MoodleService
                MoodleService moodleService = new MoodleService();

                // Test 1: Fetch and print the current user ID
                int userId = moodleService.getCurrentUserId();
                if (userId != -1) {
                    System.out.println("User ID: " + userId);
                } else {
                    System.out.println("Failed to fetch user ID.");
                }

                // Test 2: Fetch and print the user's enrolled courses
                List<Course> courses = moodleService.getUserCourses();
                if (courses != null && !courses.isEmpty()) {
                    System.out.println("Courses the user is enrolled in:");
                    for (Course course : courses) {
                        System.out.println("Course ID: " + course.getId() + ", Course Name: " + course.getName() );
                    }
                } else {
                    System.out.println("No courses found or failed to fetch courses.");
                }
            } else {
                System.out.println("Failed to retrieve token.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
