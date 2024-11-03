package springbootapp.model;

import java.util.List;

public class Student {
    private int id;
    private String fullName;
    private List<GradeItem> gradeItems;  // List to store grade items
    private List<Integer> roles;          // List to store roles
    private List<Note> notes;

    // Constructor
    public Student(int id, String fullName, List<Integer> roles) {
        this.id = id;
        this.fullName = fullName;
        this.roles = roles;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    // Getters and Setters for Notes
    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
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

    @Override
    public String toString() {
        return "Student{id=" + id + ", fullName='" + fullName + "', gradeItems=" + gradeItems + ", roles=" + roles + ", notes=" + notes + "}";
    }
}
