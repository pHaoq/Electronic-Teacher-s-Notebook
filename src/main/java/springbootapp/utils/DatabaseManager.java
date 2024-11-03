package springbootapp.utils;

import springbootapp.model.Note;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/notes.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create table with the new schema
    public static void createNewTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        note_text TEXT NOT NULL,
                        note_colour TEXT NOT NULL,
                        date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                     );
                     """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'notes' has been created with the new schema.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a new note into the database
    public static void insertNote(int studentId, int courseId, String noteText, String noteColour) {
        String sql = "INSERT INTO notes(student_id, course_id, note_text, note_colour) VALUES(?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, noteText);
            pstmt.setString(4, noteColour);
            pstmt.executeUpdate();
            System.out.println("A new note has been inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Retrieve all notes from the database
    public static void selectAllNotes() {
        String sql = "SELECT id, student_id, course_id, note_text, note_colour, date FROM notes";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getInt("student_id") + "\t" +
                        rs.getInt("course_id") + "\t" +
                        rs.getString("note_text") + "\t" +
                        rs.getString("note_colour") + "\t" +
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static List<Note> selectNotesByStudentAndCourse(int studentId, int courseId) {
        String sql = "SELECT id, student_id, course_id, note_text, note_colour, date FROM notes WHERE student_id = ? AND course_id = ?";
        List<Note> notes = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();

            // Loop through the result set and add each note to the list
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("note_text");
                String colour = rs.getString("note_colour");

                // Create a new Note object and add it to the list
                Note note = new Note(id, studentId, courseId, text, colour);
                notes.add(note);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return notes;
    }

    public static void main(String[] args) {
        // Create the database table and insert a sample note for testing
        createNewTable();
        insertNote(1, 101, "Test note for student 1", "blue");
        selectAllNotes();
    }
}
