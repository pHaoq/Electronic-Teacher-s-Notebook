package springbootapp.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import springbootapp.model.Course;
import springbootapp.service.MoodleService;

import java.io.IOException;
import java.util.List;

public class CoursesController {

    @FXML private TableView<Course> coursesTable; // Tabelle für Kurse
    @FXML private TableColumn<Course, String> courseIdColumn; // Spalte für Kurs-ID
    @FXML private TableColumn<Course, String> courseNameColumn; // Spalte für Kursnamen
    @FXML private VBox mainContainer; // Optional: für den Logout-Button

    private MainView mainView;
    private final MoodleService moodleService = new MoodleService(); // MoodleService initialisieren

    /**
     * Setter für MainView, um die Verbindung herzustellen.
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Initialisierung der Tabelle und Konfiguration.
     */
    @FXML
    public void initialize() {
        // Konfiguration der TableView-Spalten
        courseIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Kurse laden und der Tabelle hinzufügen
        loadCourses();

        // Event-Handler für Doppelklick in der Tabelle
        coursesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doppelklick
                handleCourseSelection();
            }
        });
    }

    /**
     * Lädt die Kurse und fügt sie der Tabelle hinzu.
     */
    private void loadCourses() {
        try {
            List<Course> courses = moodleService.getUserCourses(); // Daten von Moodle abrufen
            if (courses != null && !courses.isEmpty()) {
                ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
                coursesTable.setItems(courseList);
            } else {
                System.out.println("Keine Kurse gefunden oder Fehler beim Abrufen der Kurse.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Fehler beim Abrufen der Kursdaten.");
            e.printStackTrace();
        }
    }

    /**
     * Event-Handler für die Auswahl eines Kurses in der Tabelle.
     */
    @FXML
    private void handleCourseSelection() {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                if (selectedCourse.getId() != 0) {
                    mainView.showStudentGradesView(selectedCourse.getId());
                } else {
                    System.out.println("Kein gültiger Kurs ausgewählt.");
                }
            } catch (IOException e) {
                System.out.println("Fehler beim Laden der StudentGrades-Ansicht.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Kein Kurs ausgewählt.");
        }
    }


    /**
     * Event-Handler für den Logout-Button.
     */
    @FXML
    private void handleLogoutButton() {
        try {
            mainView.showLoginView(); // Zurück zur Login-Ansicht
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Login-Ansicht.");
            e.printStackTrace();
        }
    }
}
