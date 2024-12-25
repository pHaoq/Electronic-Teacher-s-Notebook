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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class NotesController {
    @FXML private TextField noteTextField;
    @FXML private ComboBox<String> noteColorComboBox;
    @FXML private Button addNoteButton;
    @FXML private TableView<Note> notesTable;
    @FXML private TableColumn<Note, String> textColumn;
    @FXML private TableColumn<Note, String> colorColumn;
    @FXML private TableColumn<Note, String> dateColumn;
    @FXML private TableColumn<Note, Void> actionColumn;

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
        // Add types without parentheses
        noteColorComboBox.getItems().addAll("Organizatory", "Negative Behavior", "Positive Behavior");

        noteColorComboBox.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Use same colors as in the table rows
                    String color = mapTypeToColor(item);
                    setStyle("-fx-background-color: " + getColorStyle(color) + "; -fx-text-fill: black;");
                    setText(item);
                }
            }
        });

        noteColorComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String color = mapTypeToColor(item);
                    setStyle("-fx-background-color: " + getColorStyle(color) + "; -fx-text-fill: black;");
                    setText(item);
                }
            }
        });

        noteColorComboBox.setPromptText("Select note type");
    }

    private void setupTable() {
        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));

        // Custom cell value factory for displaying types instead of colors
        colorColumn.setCellValueFactory(cellData -> {
            String color = cellData.getValue().getColour();
            return new javafx.beans.property.SimpleStringProperty(mapColorToType(color));
        });

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("X");
            private final Button editButton = new Button("Edit");
            private final Button upButton = new Button("↑");
            private final Button downButton = new Button("↓");
            private final HBox actionButtons = new HBox(5, deleteButton, editButton, upButton, downButton);

            {
                deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteButton.setOnAction(event -> {
                    Note note = getTableView().getItems().get(getIndex());
                    handleDeleteNote(note);
                });

                editButton.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-weight: bold;");
                editButton.setOnAction(event -> {
                    Note note = getTableView().getItems().get(getIndex());
                    handleEditNote(note);
                });

                upButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-weight: bold;");
                upButton.setOnAction(event -> {
                    int currentIndex = getIndex();
                    if (currentIndex > 0) {
                        Collections.swap(notesData, currentIndex, currentIndex - 1);
                        notesTable.refresh();
                    }
                });

                downButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-font-weight: bold;");
                downButton.setOnAction(event -> {
                    int currentIndex = getIndex();
                    if (currentIndex < notesData.size() - 1) {
                        Collections.swap(notesData, currentIndex, currentIndex + 1);
                        notesTable.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });

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
        String selectedType = noteColorComboBox.getValue();

        if (!noteText.isEmpty() && selectedType != null) {
            String color = mapTypeToColor(selectedType);
            String currentDate = getCurrentDateTime();
            DatabaseManager.insertNote(studentId, courseId, noteText, color);

            Note newNote = new Note(0, studentId, courseId, noteText, color, currentDate);
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

    private void handleEditNote(Note note) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Note");
        dialog.setHeaderText("Edit the selected note");

        TextArea textArea = new TextArea(note.getText());
        textArea.setWrapText(true);
        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle dialog result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String newText = textArea.getText().trim();
                if (!newText.isEmpty()) {
                    note.setText(newText);
                    String newDate = getCurrentDateTime();
                    note.setDate(newDate);
                    DatabaseManager.updateNoteText(note.getId(), newText, newDate);
                    notesTable.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Note text cannot be empty.");
                    alert.show();
                }
            }
        });
    }

    private String mapTypeToColor(String type) {
        return switch (type.toLowerCase()) {
            case "organizatory" -> "blue";
            case "negative behavior" -> "red";
            case "positive behavior" -> "green";
            default -> "transparent";
        };
    }

    private String mapColorToType(String color) {
        return switch (color.toLowerCase()) {
            case "blue" -> "Organizatory";
            case "red" -> "Negative Behavior";
            case "green" -> "Positive Behavior";
            default -> "Unknown";
        };
    }

    private String getColorStyle(String color) {
        return switch (color.toLowerCase()) {
            case "blue" -> "rgba(173, 216, 230, 0.5)";
            case "red" -> "rgba(255, 182, 193, 0.5)";
            case "green" -> "rgba(144, 238, 144, 0.5)";
            default -> "transparent";
        };
    }

    private String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @FXML
    private void handleBackButton() {
        try {
            mainView.showStudentGradesView(courseId);
        } catch (IOException e) {
            System.out.println("Error loading courses view.");
            e.printStackTrace();
        }
    }
}
