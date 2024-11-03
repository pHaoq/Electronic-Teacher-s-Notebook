package springbootapp.gui;

import springbootapp.moodle.MoodleAPI;
import springbootapp.moodle.TokenManager;
import springbootapp.service.MoodleService;
import springbootapp.utils.DatabaseManager;
import springbootapp.model.Course;
import springbootapp.model.Student;
import springbootapp.model.GradeItem;
import springbootapp.model.Note;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main_test {
    public static void main(String[] args) {
        // Initialize MoodleService
        MoodleService moodleService = new MoodleService();
        MoodleAPI moodleAPI = new MoodleAPI();  // Instantiate the MoodleAPI to use the authenticate method
        Scanner scanner = new Scanner(System.in);

        try {
            // Prompt the user for credentials
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            // Test the authenticate function
            boolean isAuthenticated = moodleAPI.authenticate(username, password);

            if (isAuthenticated) {
                System.out.println("Authentication successful. Token has been saved.");

                // Fetch and print the courses where the user is enrolled
                List<Course> userCourses = moodleService.getUserCourses();
                if (userCourses != null && !userCourses.isEmpty()) {
                    System.out.println("Courses where the user is enrolled:");
                    for (Course course : userCourses) {
                        System.out.println("Course ID: " + course.getId() + ", Course Name: " + course.getName());
                    }

                    // Ask user to select a course to view grades
                    System.out.print("Please enter the course ID for which you want to retrieve the students and their grades: ");
                    int selectedCourseId = scanner.nextInt();  // Capture the course ID
                    scanner.nextLine();  // Consume newline

                    // Fetch and print all students' grades in the selected course
                    List<Student> studentsWithGrades = moodleService.getAllStudentsWithGrades(selectedCourseId);
                    if (studentsWithGrades != null && !studentsWithGrades.isEmpty()) {
                        System.out.println("Grades for all enrolled students in course ID " + selectedCourseId + ":");
                        for (Student student : studentsWithGrades) {
                            System.out.println("Student: " + student.getFullName() + " (ID: " + student.getId() + ") Role: " + student.getRoles());
                            System.out.println("Grades:");
                            List<GradeItem> gradeItems = student.getGradeItems();
                            if (gradeItems != null && !gradeItems.isEmpty()) {
                                for (GradeItem gradeItem : gradeItems) {
                                    System.out.println("  - " + gradeItem.getItemName() + ": " + gradeItem.getGrade());
                                }
                            } else {
                                System.out.println("  No grades available for this student.");
                            }
                            System.out.println();  // Add a blank line between students for readability
                        }

                        // Prompt user to add a note for a specific student in this course
                        System.out.print("Enter the student ID to whom you want to add a note: ");
                        int studentId = scanner.nextInt();
                        scanner.nextLine();  // Consume newline

                        System.out.print("Do you want to add a note for this student? (yes/no): ");
                        String addNoteResponse = scanner.nextLine().trim().toLowerCase();
                        if (addNoteResponse.equals("yes")) {
                            System.out.print("Enter the note text: ");
                            String noteText = scanner.nextLine();

                            System.out.print("Enter the note colour: ");
                            String noteColour = scanner.nextLine();

                            // Add note to the database
                            DatabaseManager.insertNote(studentId, selectedCourseId, noteText, noteColour);
                            System.out.println("Note added successfully.");
                        }

                        // Retrieve and display notes for the specified student and course
                        System.out.println("Retrieving notes for student ID " + studentId + " in course ID " + selectedCourseId + "...");
                        List<Note> notes = DatabaseManager.selectNotesByStudentAndCourse(studentId, selectedCourseId);
                        if (!notes.isEmpty()) {
                            System.out.println("Notes for student ID " + studentId + " in course ID " + selectedCourseId + ":");
                            for (Note note : notes) {
                                System.out.println(note);
                            }
                        } else {
                            System.out.println("No notes found for this student in the specified course.");
                        }

                    } else {
                        System.out.println("No grades found or insufficient permissions to view grades.");
                    }

                } else {
                    System.out.println("No courses found or failed to fetch courses.");
                }
            } else {
                System.out.println("Authentication failed. Please check your username and password.");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("An error occurred during the process.");
            e.printStackTrace();
        }
    }
}
