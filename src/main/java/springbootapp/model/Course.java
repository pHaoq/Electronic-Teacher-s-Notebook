package springbootapp.model;

public class Course {
    private int id;
    private String name;
    private String role;  // Field to store the user's role in the course

    // Constructor with id and name parameters
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
        this.role = null;  // Role can be set later
    }

    // Constructor with id, name, and role parameters
    public Course(int id, String name, String role) {
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
