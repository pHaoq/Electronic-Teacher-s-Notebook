package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import springbootapp.moodle.MoodleAPI;

import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label loginStatusLabel;

    private MoodleAPI moodleAPI = new MoodleAPI();
    private MainView mainView;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            boolean isAuthenticated = moodleAPI.authenticate(username, password);
            if (isAuthenticated) {
                loginStatusLabel.setText("Login successful!");
                mainView.showCoursesView();  // Switch to courses view
            } else {
                loginStatusLabel.setText("Authentication failed. Try again.");
            }
        } catch (IOException | InterruptedException e) {
            loginStatusLabel.setText("An error occurred during authentication.");
            e.printStackTrace();
        }
    }
}
