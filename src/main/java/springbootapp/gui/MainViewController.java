package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import springbootapp.moodle.MoodleAPI;
import springbootapp.service.MoodleService;

import java.io.IOException;

public class MainViewController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginStatusLabel;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Authenticate using MoodleAPI
            MoodleAPI moodleAPI = new MoodleAPI();
            String token = moodleAPI.authenticate(username, password);

            if (token != null) {
                loginStatusLabel.setText("Login successful!");

                // Use MoodleService to retrieve user data and courses
                MoodleService moodleService = new MoodleService();
                int userId = moodleService.getCurrentUserId(token);

                if (userId != -1) {
                    // Retrieve and display the user's courses
                    moodleService.getUserCourses(token, userId).forEach(course -> {
                        System.out.println("Course ID: " + course.get("id").getAsInt());
                        System.out.println("Course Full Name: " + course.get("fullname").getAsString());
                        System.out.println("Course Short Name: " + course.get("shortname").getAsString());
                        System.out.println("-------------");
                    });
                } else {
                    loginStatusLabel.setText("Failed to retrieve the user ID.");
                }
            } else {
                loginStatusLabel.setText("Login failed. Token not retrieved.");
            }
        } catch (IOException e) {
            loginStatusLabel.setText("Error connecting to Moodle: " + e.getMessage());
        } catch (InterruptedException e) {
            loginStatusLabel.setText("Operation was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
