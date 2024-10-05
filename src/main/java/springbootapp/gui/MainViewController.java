package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import springbootapp.moodle.MoodleAPI;

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
            MoodleAPI moodleAPI = new MoodleAPI();

            // Authenticate and retrieve the token
            String token = moodleAPI.authenticate(username, password);

            if (token != null) {
                loginStatusLabel.setText("Login successful!");

                // Get the current user ID
                int userId = moodleAPI.getCurrentUserId(token);

                if (userId != -1) {
                    // Retrieve and print the courses
                    moodleAPI.getUserCourses(token, userId).forEach(course -> {
                        System.out.println("Course ID: " + course.get("id").getAsInt());
                        System.out.println("Course Full Name: " + course.get("fullname").getAsString());
                        System.out.println("Course Short Name: " + course.get("shortname").getAsString());
                        System.out.println("-------------");
                    });
                } else {
                    System.out.println("Failed to retrieve the user ID.");
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
