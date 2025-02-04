package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.entity.Homework;
import com.example.ThinkingPotato.repository.HomeworkRepository;
import com.example.ThinkingPotato.repository.TeacherStudentRepository;
import com.example.ThinkingPotato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HomeworkService {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private UserRepository userRepository; // ✅ Repository to find student_id from email

    @Autowired
    private TeacherStudentRepository teacherStudentRepository; // ✅ Repository to find student ID in teacher-student mapping

    public Homework addHomework(Homework homework) {
        return homeworkRepository.save(homework);
    }

    public void deleteHomework(Long id) {
        homeworkRepository.deleteById(id);
    }

    public List<Homework> getHomeworkByTeacherAndStudent(Long teacherId, Long studentId) {
        return homeworkRepository.findByTeacherIdAndStudentId(teacherId, studentId);
    }

    public Homework updateHomeworkCompletion(Long id, boolean completed) {
        Homework homework = homeworkRepository.findById(id).orElseThrow();
        homework.setCompleted(completed);
        return homeworkRepository.save(homework);
    }
}



