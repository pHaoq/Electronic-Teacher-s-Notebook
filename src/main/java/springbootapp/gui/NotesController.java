package springbootapp.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import springbootapp.model.Note;
import springbootapp.utils.DatabaseManager;

import java.io.IOException;
import java.util.List;

public class NotesController {
    @FXML private TextField noteTextField;
    @FXML private ComboBox<String> noteColorComboBox; // Dropdown for color selection
    @FXML private Button addNoteButton;
    @FXML private TableView<Note> notesTable; // Table for displaying notes
    @FXML private TableColumn<Note, String> textColumn;
    @FXML private TableColumn<Note, String> colorColumn;
    @FXML private TableColumn<Note, Void> actionColumn; // Column for delete button

    private MainView mainView;
    private int studentId;
    private int courseId;

    private final ObservableList<Note> notesData = FXCollections.observableArrayList();

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;

        setupColorComboBox();
        setupTable();
        displayNotes();
    }

    private void setupColorComboBox() {
        // Add descriptive names for colors
        noteColorComboBox.getItems().addAll(
                "Organizatory (Blue)",
                "Negative Behavior (Red)",
                "Positive Behavior (Green)"
        );

        noteColorComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String color = extractColorFromSelection(item);
                    HBox colorBox = new HBox();
                    colorBox.setStyle("-fx-background-color: " + color + "; -fx-min-width: 20px; -fx-min-height: 20px; -fx-border-color: black;");
                    setGraphic(colorBox);
                    setText(item);
                }
            }
        });
        noteColorComboBox.setPromptText("Select note type");
    }

    private void setupTable() {
        // Bind table columns to Note properties
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("colour"));

        // Add delete button column
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("X");

            {
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteButton.setOnAction(event -> {
                    Note note = getTableView().getItems().get(getIndex());
                    handleDeleteNote(note);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Add row styling based on color
        notesTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (note == null || empty) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: " + getColorStyle(note.getColour()) + ";");
                }
            }
        });

        notesTable.setItems(notesData);
    }

    private void displayNotes() {
        notesData.clear();
        List<Note> notes = DatabaseManager.selectNotesByStudentAndCourse(studentId, courseId);
        notesData.addAll(notes);
    }

    @FXML
    public void handleAddNote() {
        String noteText = noteTextField.getText().trim();
        String selectedColor = noteColorComboBox.getValue();

        if (!noteText.isEmpty() && selectedColor != null) {
            String color = extractColorFromSelection(selectedColor);
            DatabaseManager.insertNote(studentId, courseId, noteText, color);

            // Add the new note to the table and clear the fields
            Note newNote = new Note(0, studentId, courseId, noteText, color); // ID is not needed for UI
            notesData.add(newNote);

            noteTextField.clear();
            noteColorComboBox.setValue(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Both note text and note type are required.");
            alert.show();
        }
    }

    private void handleDeleteNote(Note note) {
        DatabaseManager.deleteNoteById(note.getId());
        notesData.remove(note);
    }

    private String extractColorFromSelection(String selection) {
        if (selection.startsWith("Organizatory")) {
            return "blue";
        } else if (selection.startsWith("Negative Behavior")) {
            return "red";
        } else if (selection.startsWith("Positive Behavior")) {
            return "green";
        }
        return "none";
    }

    private String getColorStyle(String color) {
        return switch (color.toLowerCase()) {
            case "blue" -> "rgba(173, 216, 230, 0.5)"; // Light Blue
            case "red" -> "rgba(255, 182, 193, 0.5)"; // Light Red
            case "green" -> "rgba(144, 238, 144, 0.5)"; // Light Green
            default -> "transparent";
        };
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
