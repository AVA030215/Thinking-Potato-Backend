package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

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
                // Check role or email to differentiate user types
                if ("teacher".equals(user.getRole())) { // If role column exists
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
    public ResponseEntity<?> getUserByEmail(@PathVariable(name="email") String email) {
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
}

