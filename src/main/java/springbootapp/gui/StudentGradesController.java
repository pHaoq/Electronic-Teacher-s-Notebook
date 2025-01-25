package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import springbootapp.model.GradeItem;
import springbootapp.model.Student;
import springbootapp.service.MoodleService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class StudentGradesController {

    @FXML private Accordion studentsAccordion;
    @FXML private TextField searchField;
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

    private void loadStudentData() {
        try {
            List<Student> students = moodleService.getAllStudentsWithGrades(selectedCourseId);
            updateAccordion(students);
        } catch (IOException | InterruptedException e) {
            System.out.println("Ein Fehler ist beim Abrufen der Studentendaten aufgetreten.");
            e.printStackTrace();
        }
    }

    private void updateAccordion(List<Student> students) {
        studentsAccordion.getPanes().clear();

        for (Student student : students) {
            TitledPane studentPane = new TitledPane();
            studentPane.setText(student.getFullName());
            studentPane.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");

            VBox container = new VBox();
            container.setSpacing(10);
            container.setStyle("-fx-padding: 15; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");

            TableView<GradeItem> gradesTable = new TableView<>();
            gradesTable.setPrefWidth(500);

            TableColumn<GradeItem, String> assignmentColumn = new TableColumn<>("Assignment");
            assignmentColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
            assignmentColumn.setPrefWidth(300);

            TableColumn<GradeItem, String> gradeColumn = new TableColumn<>("Grade");
            gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
            gradeColumn.setPrefWidth(200);

            gradesTable.getColumns().addAll(assignmentColumn, gradeColumn);

            if (student.getGradeItems() != null && !student.getGradeItems().isEmpty()) {
                gradesTable.getItems().addAll(student.getGradeItems());
            } else {
                gradesTable.setPlaceholder(new Label("No grades available"));
            }

            gradesTable.setPrefHeight(200); // Setze eine Standardhöhe für die Tabelle
            gradesTable.setMaxHeight(Double.MAX_VALUE); // Ermögliche dynamisches Wachstum

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(gradesTable);
            scrollPane.setFitToWidth(true);

            Button addCommentButton = new Button("Add Comment");
            addCommentButton.setOnAction(event -> handleAddComment(student));

            container.getChildren().addAll(scrollPane, addCommentButton);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            studentPane.setContent(container);
            studentsAccordion.getPanes().add(studentPane);
        }
    }

    private void handleAddComment(Student student) {
        try {
            mainView.showNotesView(student.getId(), selectedCourseId);
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Notizenansicht.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        try {
            List<Student> allStudents = moodleService.getAllStudentsWithGrades(selectedCourseId);
            List<Student> filteredStudents = allStudents.stream()
                    .filter(student -> student.getFullName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            updateAccordion(filteredStudents);
        } catch (IOException | InterruptedException e) {
            System.out.println("Fehler beim Abrufen der Studentendaten.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            mainView.showCoursesView();
        } catch (IOException e) {
            System.out.println("Fehler beim Laden der Kursübersicht.");
            e.printStackTrace();
        }
    }
}
