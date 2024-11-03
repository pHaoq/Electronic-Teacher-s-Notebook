package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import springbootapp.model.Course;
import springbootapp.service.MoodleService;

import java.io.IOException;
import java.util.List;

public class CoursesController {
    @FXML private ListView<Course> coursesListView;  // Changed to ListView<Course>

    private MoodleService moodleService = new MoodleService();
    private MainView mainView;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize() {
        try {
            List<Course> courses = moodleService.getUserCourses();
            ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
            coursesListView.setItems(courseList);  // Set course objects directly

            coursesListView.setOnMouseClicked(event -> {
                Course selectedCourse = coursesListView.getSelectionModel().getSelectedItem();
                if (selectedCourse != null) {
                    try {
                        mainView.showStudentGradesView(selectedCourse.getId());  // Switch to student grades view
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred while fetching courses.");
            e.printStackTrace();
        }
    }
}
