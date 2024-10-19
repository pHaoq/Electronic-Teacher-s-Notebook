package springbootapp.model;

public class Course {
    private int id;
    private String name;
    private String role;  // New field to store the user's role in the course

    // Constructor
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
        this.role = role;  // Initialize the role
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    // Override toString() for easy printing
    @Override
    public String toString() {
        return "Course{id=" + id + ", name='" + name + "', role='" + role + "'}";
    }
}
