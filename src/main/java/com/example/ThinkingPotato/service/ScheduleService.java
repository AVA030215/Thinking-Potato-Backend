package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.dto.ScheduleResponse;
import com.example.ThinkingPotato.dto.ScheduleRequest;
import com.example.ThinkingPotato.entity.Schedule;
import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.repository.ScheduleRepository;
import com.example.ThinkingPotato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Create a new schedule
    public Schedule createSchedule(ScheduleRequest request) {
        User teacher = userRepository.findOptionalByEmail(request.getTeacherEmail())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with email: " + request.getTeacherEmail()));

        User student = userRepository.findOptionalByEmail(request.getStudentEmail())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + request.getStudentEmail()));

        Schedule schedule = new Schedule();
        schedule.setTeacher(teacher);
        schedule.setStudent(student);
        schedule.setStartDate(request.getStartDate());
        schedule.setEndDate(request.getEndDate());
        schedule.setRepetition(request.getRepetition() != null ? request.getRepetition() : 1);
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setLessonType(request.getLessonType());
        schedule.setAddress(request.getAddress());

        return scheduleRepository.save(schedule);
    }

    // ✅ Get weekly schedule
    public Map<String, List<ScheduleResponse>> getWeeklySchedule(String teacherEmail) {
        User teacher = userRepository.findOptionalByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with email: " + teacherEmail));

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // Fetch all schedules for the teacher (including past repeating schedules)
        List<Schedule> schedules = scheduleRepository.findByTeacher(teacher)
                .stream()
                .filter(schedule ->
                        // Include schedules that either start this week OR are repeating into this week
                        (schedule.getStartDate().isBefore(endOfWeek) && schedule.getEndDate().isAfter(startOfWeek))
                )
                .collect(Collectors.toList());

        // Map to store grouped schedule responses
        Map<String, List<ScheduleResponse>> response = new LinkedHashMap<>();

        for (Schedule schedule : schedules) {
            LocalDate scheduleDate = schedule.getStartDate();

            // Iterate over repeating occurrences
            while (!scheduleDate.isAfter(schedule.getEndDate()) && scheduleDate.isBefore(endOfWeek.plusDays(1))) {
                if (!scheduleDate.isBefore(startOfWeek)) {
                    response
                            .computeIfAbsent(scheduleDate.toString(), k -> new ArrayList<>())
                            .add(convertToResponse(schedule));
                }
                scheduleDate = scheduleDate.plusWeeks(schedule.getRepetition()); // ✅ Increment by repetition interval
            }
        }

        return response;
    }



    // ✅ Convert Schedule entity to DTO
    private ScheduleResponse convertToResponse(Schedule schedule) {
        String studentColor = schedule.getStudent().getColorCode();
        if (studentColor == null || studentColor.isEmpty()) {
            studentColor = "#f0f0f0"; // Default color if missing
        }

        return new ScheduleResponse(
                schedule.getId(),
                schedule.getStudent().getFirstName() + " " + schedule.getStudent().getLastName(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLessonType(),
                schedule.getAddress(),
                studentColor.startsWith("#") ? studentColor : "#" + studentColor
        );
    }

    // ✅ Update an existing schedule
    public Schedule updateSchedule(Long scheduleId, ScheduleRequest updatedSchedule) {
        return scheduleRepository.findById(scheduleId).map(schedule -> {
            schedule.setStartDate(updatedSchedule.getStartDate());
            schedule.setStartTime(updatedSchedule.getStartTime());
            schedule.setEndTime(updatedSchedule.getEndTime());
            schedule.setLessonType(updatedSchedule.getLessonType());
            schedule.setAddress(updatedSchedule.getAddress());
            return scheduleRepository.save(schedule);
        }).orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
    }

    // ✅ Delete a single occurrence
    public void deleteSingleOccurrence(Long scheduleId, LocalDate occurrenceDate) {
        if (scheduleId == null || scheduleId <= 0) {
            throw new IllegalArgumentException("Invalid schedule ID: " + scheduleId);
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        if (schedule.getStartDate().equals(occurrenceDate)) {
            scheduleRepository.delete(schedule);
        } else {
            throw new IllegalArgumentException("No matching schedule found for the given date.");
        }
    }


    public void deleteAllOccurrences(Long scheduleId) {
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            throw new IllegalArgumentException("Schedule not found with ID: " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }


    // ✅ Get schedule by ID for editing
    public ScheduleResponse getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));

        return new ScheduleResponse(
                schedule.getId(),
                schedule.getStudent().getFirstName() + " " + schedule.getStudent().getLastName(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLessonType(),
                schedule.getAddress(),
                schedule.getStudent().getColorCode().startsWith("#") ? schedule.getStudent().getColorCode() : "#" + schedule.getStudent().getColorCode()
        );
    }
}







