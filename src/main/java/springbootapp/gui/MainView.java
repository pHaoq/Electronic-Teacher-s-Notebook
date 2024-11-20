package springbootapp.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        showLoginView();
    }

    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController();
        controller.setMainView(this);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public void showCoursesView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("courses.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CoursesController controller = fxmlLoader.getController();
        controller.setMainView(this);
        stage.setTitle("Courses");
        stage.setScene(scene);
        stage.show();
    }

    public void showStudentGradesView(int courseId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("notes.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        StudentGradesController controller = fxmlLoader.getController();
        controller.setMainView(this);
        controller.initialize(courseId);
        stage.setTitle("Student Grades");
        stage.setScene(scene);
        stage.show();
    }

    public void showNotesView(int studentId, int courseId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("notes.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        NotesController controller = fxmlLoader.getController();
        controller.setMainView(this);
        controller.initialize(studentId, courseId);
        stage.setTitle("Notes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
