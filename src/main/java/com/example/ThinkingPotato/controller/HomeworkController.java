package com.example.ThinkingPotato.controller;

import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.entity.Homework;
import com.example.ThinkingPotato.service.HomeworkService;
import com.example.ThinkingPotato.repository.UserRepository;
import com.example.ThinkingPotato.repository.HomeworkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/homework")
@CrossOrigin(origins = "http://localhost:3000") // Adjust for frontend
public class HomeworkController {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private HomeworkService homeworkService;


    // ✅ Fetch homework by teacherEmail and studentEmail (Just like schedule)
    @GetMapping("/student-id/{studentId}")
    public ResponseEntity<List<Homework>> getHomeworkByStudentId(@PathVariable Long studentId) {
        List<Homework> homeworkList = homeworkRepository.findByStudentId(studentId);
        return ResponseEntity.ok(homeworkList);
    }

    @GetMapping("/teacher/{teacherEmail}/student/{studentId}")
    public ResponseEntity<List<Homework>> getHomeworkByTeacherAndStudent(
            @PathVariable String teacherEmail,
            @PathVariable Long studentId) {

        // ✅ Find teacher by email (If not found, throw IllegalArgumentException)
        User teacher = userRepository.findOptionalByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("❌ Teacher not found with email: " + teacherEmail));

        // ✅ Find student by ID (If not found, throw IllegalArgumentException)
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("❌ Student not found with ID: " + studentId));

        // ✅ Fetch homework
        List<Homework> homeworkList = homeworkRepository.findByTeacherAndStudent(teacher, student);
        return ResponseEntity.ok(homeworkList);
    }




    // ✅ Add new homework
    @PostMapping
    public ResponseEntity<Homework> addHomework(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("📥 Received request payload: " + payload);
            System.out.println("✅ Checking Keys: " + payload.keySet()); // 🛠 디버깅용 로그 추가

            Object studentIdObj = payload.get("studentId");

            // ✅ studentId 변환 (String 또는 Number 가능)
            Long studentId;
            if (studentIdObj instanceof Number) {
                studentId = ((Number) studentIdObj).longValue();
            } else if (studentIdObj instanceof String) {
                studentId = Long.parseLong((String) studentIdObj); // ✅ String → Long 변환
            } else {
                throw new IllegalArgumentException("❌ Invalid studentId format: " + studentIdObj);
            }

            String teacherEmail = (String) payload.get("teacherEmail");
            String title = (String) payload.get("title");

            if (teacherEmail == null || title == null || title.isBlank()) {
                System.out.println("❌ Invalid request: missing teacherEmail or title");
                return ResponseEntity.badRequest().build();
            }

            // ✅ teacherEmail을 이용해서 teacherId 조회
            User teacher = userRepository.findOptionalByEmail(teacherEmail)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Teacher not found with email: " + teacherEmail));

            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("❌ Student not found with ID: " + studentId));

            System.out.println("✅ Found Teacher ID: " + teacher.getId());

            // ✅ Homework 저장
            Homework newHomework = new Homework();
            newHomework.setTeacher(teacher);
            newHomework.setStudent(student);
            newHomework.setTitle(title);
            newHomework.setCompleted(false);

            Homework savedHomework = homeworkService.addHomework(newHomework);
            return ResponseEntity.ok(savedHomework);

        } catch (Exception e) {
            System.out.println("❌ Exception occurred: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }



    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> updateHomeworkCompletion(@PathVariable Long id, @RequestParam boolean completed) {
        Optional<Homework> optionalHomework = homeworkRepository.findById(id);
        if (optionalHomework.isPresent()) {
            Homework homework = optionalHomework.get();
            homework.setCompleted(completed);
            homeworkRepository.save(homework);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete/{homeworkId}")
    public ResponseEntity<?> deleteHomework(@PathVariable("homeworkId") Long homeworkId) {
        try {
            if (!homeworkRepository.existsById(homeworkId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Homework not found with ID: " + homeworkId);
            }

            homeworkRepository.deleteById(homeworkId);
            return ResponseEntity.ok("✅ Homework deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Error deleting homework: " + e.getMessage());
        }
    }

    @DeleteMapping("/teacher/{teacherEmail}/student/{studentId}")
    public ResponseEntity<?> deleteAllHomework(
            @PathVariable String teacherEmail,
            @PathVariable Long studentId) {

        // ✅ Find the teacher and student
        User teacher = userRepository.findOptionalByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // ✅ Fetch all homework entries for this teacher-student pair
        List<Homework> homeworkList = homeworkRepository.findByTeacherAndStudent(teacher, student);

        if (homeworkList.isEmpty()) {
            return ResponseEntity.ok("No homework found to delete.");
        }

        // ✅ Delete all homework entries
        homeworkRepository.deleteAll(homeworkList);
        return ResponseEntity.ok("All homework deleted successfully.");
    }





}



