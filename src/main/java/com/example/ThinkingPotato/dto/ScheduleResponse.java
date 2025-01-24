package com.example.ThinkingPotato.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class ScheduleResponse {
    private String studentName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    private String lessonType;
    private String address;
    private String studentColor;

    public ScheduleResponse(String studentName, LocalTime startTime, LocalTime endTime,
                            String lessonType, String address, String studentColor) {
        this.studentName = studentName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonType = lessonType;
        this.address = address;
        this.studentColor = studentColor;
    }

    // Getters and Setters
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLessonType() { return lessonType; }
    public void setLessonType(String lessonType) { this.lessonType = lessonType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStudentColor() { return studentColor; }
    public void setStudentColor(String studentColor) { this.studentColor = studentColor; }
}
