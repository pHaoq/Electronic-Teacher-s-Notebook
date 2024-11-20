package springbootapp.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import springbootapp.model.Course;

import java.io.IOException;
import java.util.List;

public class CoursesController {

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;

    private MainView mainView;

    // Standardkonstruktor (erforderlich für FXMLLoader)
    public CoursesController() {
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    @FXML
    private void initialize() {
        // Spalten mit den Eigenschaften der Course-Klasse verknüpfen
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Beispiel-Daten für die Tabelle
        List<Course> courses = List.of(
                new Course("27868", "Agile Requirements Engineering"),
                new Course("27974", "Digital Marketing BB"),
                new Course("28100", "IT Security Basics BWI 5 BB"),
                new Course("28580", "Rapid Application Development"),
                new Course("26933", "Software Engineering Project"),
                new Course("28073", "Scientific Writing and Research Methods"),
                new Course("28297", "Testkurs zu Projekt 'Electronic Teacher's Notebook'")
        );

        // Daten in die Tabelle laden
        ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
        coursesTable.setItems(courseList);

        // Doppelklick-Ereignis für die Auswahl einer Zeile
        coursesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Doppelklick
                handleCourseSelection();
            }
        });
    }

    @FXML
    private void handleCourseSelection() {
        // Holt das ausgewählte Kursobjekt
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            try {
                // Öffnet ein neues Fenster, um die Studentengrades anzuzeigen
                mainView.showStudentGradesView(Integer.parseInt(selectedCourse.getId()));
            } catch (NumberFormatException e) {
                System.err.println("Fehler beim Konvertieren der Kurs-ID: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Fehler beim Laden der Ansicht: " + e.getMessage());
            }
        }
    }
}
