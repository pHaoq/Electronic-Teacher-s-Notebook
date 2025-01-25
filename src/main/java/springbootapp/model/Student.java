package springbootapp.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String fullName;
    private List<GradeItem> gradeItems;  // Liste von GradeItems
    private List<Integer> roles;        // Liste von Rollen
    private List<Note> notes;           // Liste von Notizen

    // Konstruktor mit drei Parametern (GradeItems wird mit einer leeren Liste initialisiert)
    public Student(int id, String fullName, List<Integer> roles) {
        this.id = id;
        this.fullName = fullName;
        this.roles = roles;
        this.gradeItems = new ArrayList<>(); // Leere Liste initialisieren
        this.notes = new ArrayList<>();      // Leere Liste initialisieren
    }

    // Konstruktor mit vier Parametern
    public Student(int id, String fullName, List<GradeItem> gradeItems, List<Integer> roles) {
        this.id = id;
        this.fullName = fullName;
        this.gradeItems = gradeItems != null ? gradeItems : new ArrayList<>();
        this.roles = roles;
        this.notes = new ArrayList<>(); // Leere Liste initialisieren
    }

    // Konstruktor mit allen Parametern
    public Student(int id, String fullName, List<GradeItem> gradeItems, List<Integer> roles, List<Note> notes) {
        this.id = id;
        this.fullName = fullName;
        this.gradeItems = gradeItems != null ? gradeItems : new ArrayList<>();
        this.roles = roles;
        this.notes = notes != null ? notes : new ArrayList<>();
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<GradeItem> getGradeItems() {
        return gradeItems;
    }

    public void setGradeItems(List<GradeItem> gradeItems) {
        this.gradeItems = gradeItems;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    // Hilfsmethode für die Anzeige der GradeItems als String
    public String getGradeItemsAsString() {
        if (gradeItems == null || gradeItems.isEmpty()) {
            return "No grades available";
        }
        StringBuilder sb = new StringBuilder();
        for (GradeItem gradeItem : gradeItems) {
            sb.append(gradeItem.getItemName()).append(": ").append(gradeItem.getGrade()).append(", ");
        }
        // Entfernt das letzte Komma und Leerzeichen
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", gradeItems=" + gradeItems +
                ", roles=" + roles +
                ", notes=" + notes +
                '}';
    }
}
