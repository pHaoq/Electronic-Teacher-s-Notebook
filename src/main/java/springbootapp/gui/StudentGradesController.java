package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import springbootapp.model.Student;
import springbootapp.service.MoodleService;

import java.io.IOException;
import java.util.List;

public class StudentGradesController {
    @FXML private ListView<Student> studentsListView;  // Changed to ListView<Student>

    private MoodleService moodleService = new MoodleService();
    private int selectedCourseId;
    private MainView mainView;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize(int courseId) {
        selectedCourseId = courseId;
        try {
            List<Student> students = moodleService.getAllStudentsWithGrades(courseId);
            ObservableList<Student> studentList = FXCollections.observableArrayList(students);
            studentsListView.setItems(studentList);  // Set student objects directly

            studentsListView.setOnMouseClicked(event -> {
                Student selectedStudent = studentsListView.getSelectionModel().getSelectedItem();
                if (selectedStudent != null) {
                    try {
                        mainView.showNotesView(selectedStudent.getId(), selectedCourseId);  // Switch to notes view
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while retrieving students' grades.");
            e.printStackTrace();
        }
    }
}
