package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.dto.ScheduleResponse;
import com.example.ThinkingPotato.dto.ScheduleRequest;
import com.example.ThinkingPotato.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;



    @DeleteMapping("/delete/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable("scheduleId") String scheduleId,
            @RequestParam(name = "future", required = false, defaultValue = "false") boolean future,
            @RequestParam(name = "date", required = false) String date
    ) {
        if (scheduleId == null || scheduleId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Schedule ID is required.");
        }

        Long id;
        try {
            id = Long.parseLong(scheduleId.trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid schedule ID format: " + scheduleId);
        }

        // 🔹 If `date` is missing, return an error instead of trying to parse it
        if (!future && (date == null || date.trim().isEmpty())) {
            return ResponseEntity.badRequest().body("Missing date parameter for single occurrence deletion.");
        }

        try {
            if (future) {
                scheduleService.deleteAllOccurrences(id);
                return ResponseEntity.ok("All future occurrences deleted successfully!");
            } else {
                LocalDate occurrenceDate = LocalDate.parse(date);
                scheduleService.deleteSingleOccurrence(id, occurrenceDate);
                return ResponseEntity.ok("Single occurrence deleted successfully!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable("scheduleId") Long scheduleId,
            @RequestBody ScheduleRequest updatedSchedule) {
        try {
            return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, updatedSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }



    // ✅ Get a specific schedule for editing
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long scheduleId) {
        try {
            ScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // ✅ Add new schedule
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleRequest request) {
        try {
            return ResponseEntity.ok(scheduleService.createSchedule(request));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error: " + e.getMessage());
        }
    }

    // ✅ Get weekly schedule
// ✅ Get weekly schedule
    @GetMapping("/weekly/{teacherEmail}")
    public ResponseEntity<Map<String, List<ScheduleResponse>>> getWeeklySchedule(@PathVariable("teacherEmail") String teacherEmail) {
        try {
            Map<String, List<ScheduleResponse>> weeklySchedule = scheduleService.getWeeklySchedule(teacherEmail);
            return ResponseEntity.ok(weeklySchedule);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Avoid returning a plain error string
        }
    }



}





