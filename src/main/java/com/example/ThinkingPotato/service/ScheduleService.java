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

    public Schedule updateSchedule(Long scheduleId, ScheduleRequest updatedSchedule) {
        return scheduleRepository.findById(scheduleId).map(schedule -> {
            schedule.setStartDate(updatedSchedule.getStartDate());
            schedule.setEndDate(updatedSchedule.getEndDate());
            schedule.setRepetition(updatedSchedule.getRepetition());
            schedule.setStartTime(updatedSchedule.getStartTime());
            schedule.setEndTime(updatedSchedule.getEndTime());
            schedule.setLessonType(updatedSchedule.getLessonType());
            schedule.setAddress(updatedSchedule.getAddress());
            return scheduleRepository.save(schedule);
        }).orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + scheduleId));
    }

    public void deleteSchedule(Long scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new IllegalArgumentException("Schedule not found with ID: " + scheduleId);
        }
        scheduleRepository.deleteById(scheduleId);
    }

    public Map<String, Object> getWeeklySchedule(String teacherEmail) {
        User teacher = userRepository.findOptionalByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with email: " + teacherEmail));

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<Schedule> schedules = scheduleRepository.findByTeacherAndStartDateBetween(teacher, startOfWeek, endOfWeek);

        Map<String, Object> response = new LinkedHashMap<>();
        schedules.stream()
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getStartDate().toString(),
                        TreeMap::new,
                        Collectors.mapping(this::convertToResponse, Collectors.toList())
                ))
                .forEach(response::put);

        return response;
    }

    private ScheduleResponse convertToResponse(Schedule schedule) {
        String studentColor = schedule.getStudent().getColorCode(); // ✅ Fetch from DB
        if (studentColor == null || studentColor.isEmpty()) {
            studentColor = "#f0f0f0"; // ✅ Default color if missing
        }

        return new ScheduleResponse(
                schedule.getStudent().getFirstName() + " " + schedule.getStudent().getLastName(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getLessonType(),
                schedule.getAddress(),
                schedule.getStudent().getColorCode().startsWith("#") ? schedule.getStudent().getColorCode() : "#" + schedule.getStudent().getColorCode() // ✅ Fix missing `#`
        );
    }

    private String getColorCode(Long studentId) {
        String[] colors = {"#ff6666", "#66b3ff", "#99ff99", "#ffcc99", "#c299ff"};
        return colors[(int) (studentId % colors.length)];
    }
}


