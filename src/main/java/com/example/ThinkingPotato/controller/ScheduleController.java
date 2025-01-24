package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.dto.ScheduleResponse;
import com.example.ThinkingPotato.service.ScheduleService;
import com.example.ThinkingPotato.dto.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleRequest updatedSchedule) {
        try {
            return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, updatedSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.ok("Schedule deleted successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequest request) {
        try {
            return ResponseEntity.ok(scheduleService.createSchedule(request));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/weekly/{teacherEmail}")
    public ResponseEntity<?> getWeeklySchedule(@PathVariable("teacherEmail") String teacherEmail) {
        try {
            Map<String, Object> weeklySchedule = scheduleService.getWeeklySchedule(teacherEmail);
            return ResponseEntity.ok(weeklySchedule);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching schedule: " + e.getMessage());
        }
    }
}

