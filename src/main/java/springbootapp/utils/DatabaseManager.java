package springbootapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // SQLite database URL
    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/database/notes.db";

    // Establish a connection to the SQLite database
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

    // Create a table for storing notes if it doesn't exist
    public static void createNewTable() {
        String sql = """
                     CREATE TABLE IF NOT EXISTS notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        note TEXT NOT NULL,
                        date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                     );
                     """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'notes' has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a new note into the database
    public static void insertNote(int studentId, String noteText) {
        String sql = "INSERT INTO notes(student_id, note) VALUES(?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, noteText);
            pstmt.executeUpdate();
            System.out.println("A new note has been inserted.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Retrieve all notes from the database
    public static void selectAllNotes() {
        String sql = "SELECT id, student_id, note, date FROM notes";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through the result set and print each note
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getInt("student_id") + "\t" +
                        rs.getString("note") + "\t" +
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Create the database table and insert a sample note for testing
        createNewTable();
        insertNote(1, "Test note for student 1");
        selectAllNotes();
    }
}
