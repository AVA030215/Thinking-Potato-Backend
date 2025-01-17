package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.entity.TeacherStudent;
import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.service.TeacherStudentService;
import com.example.ThinkingPotato.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherStudentService teacherStudentService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (userService.emailExists(user.getEmail())) {
                return ResponseEntity.badRequest().body("Error: Email already exists!");
            }
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            User user = userService.getUserByEmail(email);

            if (user != null && user.getPassword().equals(password)) {
                // Check role to differentiate user types
                if ("teacher".equals(user.getRole())) {
                    return ResponseEntity.ok(Map.of("message", "Login successful", "role", "teacher"));
                } else {
                    return ResponseEntity.ok(Map.of("message", "Login successful", "role", "student"));
                }
            } else {
                return ResponseEntity.status(401).body("Invalid email or password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable(name = "email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error fetching user: {}", e.getMessage(), e);
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam(name = "email") String email) {
        boolean exists = userService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/details/{email}")
    public ResponseEntity<?> getUserDetails(@PathVariable(name = "email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // New Endpoint: Add Student to Teacher
    @PostMapping("/teacher/add-student")
    public ResponseEntity<?> addStudentToTeacher(@RequestBody Map<String, String> request) {
        String teacherEmail = request.get("teacherEmail"); // Dynamically passed from frontend
        String studentEmail = request.get("studentEmail");

        if (teacherEmail == studentEmail) {
            return ResponseEntity.badRequest().body("Error: teacherEmail and studentEmail has to be different!");
        }
        if (teacherEmail == null || studentEmail == null) {
            if(teacherEmail == null && studentEmail == null) {
                return ResponseEntity.badRequest().body("Error: teacherEmail and studentEmail cannot be null");
            }
            else if(teacherEmail == null){
                return ResponseEntity.status(400).body("Teacher problem");
            }
            else {
                return ResponseEntity.status(400).body("Student problem");
            }

        }

        try {
            TeacherStudent teacherStudent = teacherStudentService.addStudentToTeacher(teacherEmail, studentEmail);
            return ResponseEntity.ok("Student added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }



    // Get Students by Teacher ID (Long)
    @GetMapping("/teacher/id/{teacherId}/students")
    public ResponseEntity<?> getStudentsForTeacherById(@PathVariable Long teacherId) {
        try {
            List<TeacherStudent> students = teacherStudentService.getStudentsForTeacher(teacherId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error fetching students by teacher ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // Get Students by Teacher Email (String)
    @GetMapping("/teacher/email/{teacherEmail}/students")
    public ResponseEntity<?> getStudentsForTeacherByEmail(@PathVariable("teacherEmail") String teacherEmail) {
        System.out.println("Fetching students for teacher email: " + teacherEmail);

        if (teacherEmail == null || teacherEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Teacher email cannot be null or empty.");
        }

        try {
            List<User> students = teacherStudentService.getStudentsForTeacherDetails(teacherEmail);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @DeleteMapping("/teacher/remove-student")
    public ResponseEntity<?> removeStudentFromTeacher(@RequestBody Map<String, String> request) {
        String teacherEmail = request.get("teacherEmail");
        String studentEmail = request.get("studentEmail");

        if (teacherEmail == null || studentEmail == null) {
            return ResponseEntity.status(400).body("Both teacherEmail and studentEmail are required.");
        }

        try {
            teacherStudentService.removeStudentFromTeacher(teacherEmail, studentEmail);
            return ResponseEntity.ok("Student removed successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }


}



