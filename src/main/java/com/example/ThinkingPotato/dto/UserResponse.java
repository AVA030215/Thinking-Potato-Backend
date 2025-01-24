package com.example.ThinkingPotato.dto;

public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mathLevel;
    private String colorCode;  // ✅ Add Color

    public UserResponse(Long id,String firstName, String lastName, String email, String mathLevel, String colorCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mathLevel = mathLevel;
        this.colorCode = colorCode;
    }

    // Getters
    public Long getId() {return id;}
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getMathLevel() { return mathLevel; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
}

