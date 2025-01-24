package com.example.ThinkingPotato.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleRequest {
    private String teacherEmail;
    private String studentEmail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer repetition; // Example: 1 = Every Week, 2 = Every 2 Weeks

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    private String lessonType; // "Online" or "In-Person"
    private String address; // Only for in-person lessons

    // Default Constructor
    public ScheduleRequest() {}

    // Constructor with Parameters
    public ScheduleRequest(String teacherEmail, String studentEmail, LocalDate startDate, LocalDate endDate,
                           Integer repetition, LocalTime startTime, LocalTime endTime, String lessonType, String address) {
        this.teacherEmail = teacherEmail;
        this.studentEmail = studentEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.repetition = repetition;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonType = lessonType;
        this.address = address;
    }

    // Getters and Setters
    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getRepetition() {
        return repetition != null ? repetition : 1;
    }


    public void setRepetition(Integer repetition) {
        this.repetition = repetition;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}



