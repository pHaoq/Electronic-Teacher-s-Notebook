package springbootapp.utils;

import springbootapp.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/notes.db";
    private static Connection conn = null;

    public static void connect() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DATABASE_URL);
                System.out.println("Persistent connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing the database connection: " + e.getMessage());
        }
    }

    public static void createNewTable() {
        connect();
        String sql = """
                     CREATE TABLE IF NOT EXISTS notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        note_text TEXT NOT NULL,
                        note_colour TEXT NOT NULL,
                        date TEXT DEFAULT CURRENT_TIMESTAMP
                     );
                     """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'notes' has been created.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void insertNote(int studentId, int courseId, String noteText, String noteColour) {
        connect();
        String sql = "INSERT INTO notes(student_id, course_id, note_text, note_colour, date) VALUES(?, ?, ?, ?, datetime('now'))";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, noteText);
            pstmt.setString(4, noteColour);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting note: " + e.getMessage());
        }
    }

    public static void updateNoteText(int noteId, String newText, String newDate) {
        connect();
        String sql = "UPDATE notes SET note_text = ?, date = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setString(2, newDate);
            pstmt.setInt(3, noteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating note: " + e.getMessage());
        }
    }

    public static List<Note> selectNotesByStudentAndCourse(int studentId, int courseId) {
        connect();
        String sql = "SELECT id, student_id, course_id, note_text, note_colour, date FROM notes WHERE student_id = ? AND course_id = ?";
        List<Note> notes = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("note_text");
                String colour = rs.getString("note_colour");
                String date = rs.getString("date");
                Note note = new Note(id, studentId, courseId, text, colour, date);
                notes.add(note);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving notes: " + e.getMessage());
        }

        return notes;
    }

    public static void deleteNoteById(int noteId) {
        connect();
        String sql = "DELETE FROM notes WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }
}
