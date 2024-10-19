package springbootapp.gui;

import springbootapp.moodle.MoodleAPI;
import springbootapp.moodle.TokenManager;
import springbootapp.service.MoodleService;
import springbootapp.model.Course;
import springbootapp.model.Student;
import springbootapp.model.GradeItem;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main_test {
    public static void main(String[] args) {
        // Initialize MoodleService
        MoodleService moodleService = new MoodleService();
        MoodleAPI moodleAPI = new MoodleAPI();  // Instantiate the MoodleAPI to use the authenticate method

        try {
            // Prompt the user for credentials
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            // Test the authenticate function
            boolean isAuthenticated = moodleAPI.authenticate(username, password);

            if (isAuthenticated) {
                System.out.println("Authentication successful. Token has been saved.");

                // Fetch the token from the file
                String token = TokenManager.loadTokenFromFile();

                if (token != null && !token.isEmpty()) {
                    System.out.println("Token successfully retrieved: " + token);
                } else {
                    System.out.println("Error: Token not found. Ensure the token is saved in the file.");
                    return;  // Exit if token is not found
                }

                // Fetch and print the courses where the user is enrolled
                List<Course> userCourses = moodleService.getUserCourses();
                if (userCourses != null && !userCourses.isEmpty()) {
                    System.out.println("Courses where the user is enrolled:");
                    for (Course course : userCourses) {
                        System.out.println("Course ID: " + course.getId() + ", Course Name: " + course.getName());
                    }

                    // Ask user if they want to retrieve grades for a specific course
                    System.out.print("Please enter the course ID for which you want to retrieve the grades: ");
                    int selectedCourseId = scanner.nextInt();  // Capture the course ID

                    // Fetch and print all students' grades in the selected course
                    List<Student> studentsWithGrades = moodleService.getAllStudentsWithGrades(selectedCourseId);
                    if (studentsWithGrades != null && !studentsWithGrades.isEmpty()) {
                        System.out.println("Grades for all enrolled students in course ID " + selectedCourseId + ":");
                        for (Student student : studentsWithGrades) {
                            System.out.println("Student: " + student.getFullName() + " (ID: " + student.getId() + ")" + " Rolle: " + student.getRoles());
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
