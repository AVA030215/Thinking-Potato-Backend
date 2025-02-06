package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.dto.UserResponse;
import com.example.ThinkingPotato.entity.TeacherStudent;
import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.service.TeacherStudentService;
import com.example.ThinkingPotato.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TeacherStudentService teacherStudentService;

    @PutMapping("/{email}/profile-photo")
    public ResponseEntity<?> updateProfilePhoto(
            @PathVariable String email,
    @RequestBody Map<String,String> request) {
        String newPhotoUrl = request.get("profilePhoto");
        if(newPhotoUrl == null || newPhotoUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Profile Photo Url is required");
        }

        User updatedUser = userService.updateProfilePhoto(email, newPhotoUrl);
        return ResponseEntity.ok(Map.of("profilePhoto", updatedUser.getProfilePhoto()));
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<?> updateUser(@PathVariable String email, @RequestBody User updatedUser) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // ✅ Update only allowed fields (not email)
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setMathLevel(updatedUser.getMathLevel());

        // ✅ Save user without checking duplicate email
        userService.updateUser(user);

        return ResponseEntity.ok(user);
    }



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

        System.out.println("🔹 Received Login Request - Email: " + email + ", Password: " + password);

        try {
            if (email == null || password == null) {
                System.out.println("❌ Email or Password is missing in the request!");
                return ResponseEntity.status(400).body("Email and password are required.");
            }

            User user = userService.getUserByEmail(email);
            if (user != null && user.getPassword().equals(password)) {
                System.out.println("✅ Login Successful for " + email);

                // 🔹 FIX: Now we include "email" in the response
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "role", user.getRole(),
                        "email", user.getEmail() // <-- ✅ This is the missing field
                ));
            } else {
                System.out.println("❌ Invalid Login Attempt for " + email);
                return ResponseEntity.status(401).body("Invalid email or password!");
            }
        } catch (Exception e) {
            System.out.println("❌ Internal Server Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String currentPassword = request.get("currentPassword");

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // ✅ Verify current password before allowing a change
        if (!user.getPassword().equals(currentPassword)) {
            return ResponseEntity.status(401).body("Incorrect password");
        }

        return ResponseEntity.ok("Password verified");
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        user.setPassword(newPassword); // ✅ Update password
        userService.updateUser(user); // ✅ Save updated user

        return ResponseEntity.ok("Password updated successfully");
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
        String teacherEmail = request.get("teacherEmail");
        String studentEmail = request.get("studentEmail");

        if (teacherEmail == null || studentEmail == null) {
            return ResponseEntity.status(400).body("Error: Both teacherEmail and studentEmail are required.");
        }

        if (teacherEmail.equals(studentEmail)) {
            return ResponseEntity.badRequest().body("Error: Teacher cannot add themselves as a student!");
        }

        try {
            if (userService.isStudentAlreadyAdded(teacherEmail, studentEmail)) {
                return ResponseEntity.status(400).body("Error: This student is already assigned to you!");
            }

            userService.addStudentToTeacher(teacherEmail, studentEmail);
            return ResponseEntity.ok("Student added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
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
            List<UserResponse> students = userService.getStudentsForTeacherDetails(teacherEmail);
            return ResponseEntity.ok(students);  // ✅ Return new UserResponse DTO with color
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/student-id/{email}")
    public ResponseEntity<Map<String, Long>> getStudentIdByEmail(@PathVariable String email) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        Optional<Long> studentIdOptional = userService.getStudentIdByEmail(decodedEmail);

        return studentIdOptional.map(studentId -> ResponseEntity.ok(Map.of("studentId", studentId)))
                .orElseGet(() -> ResponseEntity.notFound().build());
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



