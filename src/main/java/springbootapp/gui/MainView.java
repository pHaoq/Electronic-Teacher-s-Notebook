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
        showLoginView(); // Zeigt die Login-Ansicht als Startansicht
    }

    /**
     * Zeigt die Login-Ansicht.
     */
    public void showLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController controller = fxmlLoader.getController();
        controller.setMainView(this); // Verbindung mit der MainView herstellen
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Zeigt die Kursübersicht.
     */
    public void showCoursesView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("courses.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        CoursesController controller = fxmlLoader.getController();
        controller.setMainView(this); // Verbindung mit der MainView herstellen
        stage.setTitle("Courses");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Zeigt die Ansicht für die Noten der Studenten für einen bestimmten Kurs.
     *
     * @param courseId Die ID des ausgewählten Kurses
     */
    public void showStudentGradesView(int courseId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("studentsOverview.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        StudentGradesController controller = fxmlLoader.getController();
        controller.setMainView(this); // Verbindung mit der MainView herstellen
        controller.initialize(courseId); // Initialisiert mit Kurs-ID
        stage.setTitle("Student Grades");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Zeigt die Ansicht für Notizen eines bestimmten Studenten in einem bestimmten Kurs.
     *
     * @param studentId Die ID des ausgewählten Studenten
     * @param courseId  Die ID des ausgewählten Kurses
     */
    public void showNotesView(int studentId, int courseId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("notes.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        NotesController controller = fxmlLoader.getController();
        controller.setMainView(this); // Verbindung mit der MainView herstellen
        controller.initialize(studentId, courseId); // Initialisiert mit Student- und Kurs-ID
        stage.setTitle("Notes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(); // Startet die JavaFX-Anwendung
    }
}
