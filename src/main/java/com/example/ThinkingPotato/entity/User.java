package com.example.ThinkingPotato.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String mathLevel;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private String role;

    @Column(name = "color_code", nullable = false, length = 7)  // 🔹 Store color as a HEX code
    private String colorCode;

    @Column(name = "profile_photo", nullable = true, length = 255)
    private String profilePhoto = "/public/img/profile/justpotato.png";

    // Getter and Setter for all fields
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getMathLevel() {
        return mathLevel;
    }
    public void setMathLevel(String mathLevel) {
        this.mathLevel = mathLevel;
    }

    public void setRole(String role){ this.role = role;}
    public String getRole() {return role;}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User() {
        this.colorCode = generateRandomColor();
        this.profilePhoto = "/public/img/profile/thinking-potato.png";
    }

    private String generateRandomColor() {
        String[] colors = {"#ff6666", "#66b3ff", "#99ff99", "#ffcc99", "#c299ff"};
        return colors[new Random().nextInt(colors.length)];
    }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}




