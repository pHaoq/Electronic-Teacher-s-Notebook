package springbootapp.model;

public class Course {
    private String id;
    private String name;
    private String role; // Feld, um die Rolle des Benutzers im Kurs zu speichern

    // Konstruktor mit ID und Namen
    public Course(String id, String name) {
        this.id = id;
        this.name = name;
        this.role = null; // Rolle kann später gesetzt werden
    }

    // Konstruktor mit ID, Name und Rolle
    public Course(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role; // Rolle initialisieren
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) { // Akzeptiert String für Konsistenz
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Überschreibt toString() für eine einfache Ausgabe
    @Override
    public String toString() {
        return "Course{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}
