package springbootapp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import springbootapp.utils.DatabaseManager;
import springbootapp.model.Note;

import java.util.List;

public class NotesController {
    @FXML private TextArea notesDisplayArea;
    @FXML private TextField noteTextField;
    @FXML private TextField noteColorField;
    @FXML private Button addNoteButton;

    private MainView mainView;
    private int studentId;
    private int courseId;

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initialize(int studentId, int courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        displayNotes();
    }

    private void displayNotes() {
        List<Note> notes = DatabaseManager.selectNotesByStudentAndCourse(studentId, courseId);
        StringBuilder notesDisplay = new StringBuilder();
        for (Note note : notes) {
            notesDisplay.append(note).append("\n");
        }
        notesDisplayArea.setText(notesDisplay.toString());
    }

    @FXML
    public void handleAddNote() {
        String noteText = noteTextField.getText();
        String noteColor = noteColorField.getText();
        DatabaseManager.insertNote(studentId, courseId, noteText, noteColor);
        notesDisplayArea.appendText(noteText + " (Color: " + noteColor + ")\n");
        noteTextField.clear();
        noteColorField.clear();
    }
}
