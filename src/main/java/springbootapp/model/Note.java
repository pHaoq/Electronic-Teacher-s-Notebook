package springbootapp.model;

public class Note {
    private int id;
    private int studentId;
    private int courseId;
    private String text;
    private String colour;
    private String date; // Neues Feld für das Datum

    // Neuer Konstruktor mit Datum
    public Note(int id, int studentId, int courseId, String text, String colour, String date) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.text = text;
        this.colour = colour;
        this.date = date;
    }

    // Getter und Setter für alle Felder
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Note{id=" + id + ", studentId=" + studentId + ", courseId=" + courseId + ", text='" + text + "', colour='" + colour + "', date='" + date + "'}";
    }
}
