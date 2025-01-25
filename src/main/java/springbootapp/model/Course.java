package springbootapp.model;

public class Course {
    private int id; // Kurs-ID als int
    private String name;
    private String role;

    // Konstruktor mit ID als int
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
        this.role = null;
    }

    // Konstruktor mit ID, Name und Rolle
    public Course(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdAsString() {
        return String.valueOf(id); // Gibt die ID als String zurück
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

    @Override
    public String toString() {
        return "Course{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}
