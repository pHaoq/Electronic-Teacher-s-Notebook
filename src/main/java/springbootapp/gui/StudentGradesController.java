package springbootapp.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import springbootapp.model.Student;
import springbootapp.service.MoodleService;

import java.io.IOException;
import java.util.List;

public class StudentGradesController {

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> fullNameColumn;
    @FXML private TableColumn<Student, String> gradesColumn;

    private MoodleService moodleService = new MoodleService();
    private int selectedCourseId;
    private MainView mainView;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void setCourseId(int courseId) {
        this.selectedCourseId = courseId;
        loadStudentData();
    }

    @FXML
    public void initialize() {
        // Tabelle konfigurieren
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        gradesColumn.setCellValueFactory(new PropertyValueFactory<>("gradeItemsAsString"));
    }

    private void loadStudentData() {
        try {
            List<Student> students = moodleService.getAllStudentsWithGrades(selectedCourseId);
            ObservableList<Student> studentList = FXCollections.observableArrayList(students);
            studentsTable.setItems(studentList);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ein Fehler ist beim Abrufen der Studentendaten aufgetreten.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            mainView.showCoursesView(); // Zur Kursübersicht zurückkehren
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Kursübersicht.");
            e.printStackTrace();
        }
    }
}
