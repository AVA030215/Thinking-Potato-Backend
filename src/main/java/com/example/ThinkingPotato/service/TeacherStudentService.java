package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.entity.TeacherStudent;
import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.repository.TeacherStudentRepository;
import com.example.ThinkingPotato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
public class TeacherStudentService {

    @Autowired
    private TeacherStudentRepository teacherStudentRepository;

    @Autowired
    private UserRepository userRepository;

    public TeacherStudent addStudentToTeacher(String teacherEmail, String studentEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with email: " + teacherEmail);
        }

        User student = userRepository.findByEmail(studentEmail);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with email: " + studentEmail);
        }

        TeacherStudent teacherStudent = new TeacherStudent();
        teacherStudent.setTeacherId(teacher.getId());
        teacherStudent.setStudentId(student.getId());

        return teacherStudentRepository.save(teacherStudent);
    }

    public List<TeacherStudent> getStudentsForTeacher(Long teacherId) {
        return teacherStudentRepository.findByTeacherId(teacherId);
    }

    public List<User> getStudentsForTeacherDetails(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with email: " + teacherEmail);
        }

        List<TeacherStudent> teacherStudentLinks = teacherStudentRepository.findByTeacherId(teacher.getId());
        List<User> students = new ArrayList<>();

        for (TeacherStudent link : teacherStudentLinks) {
            User student = userRepository.findById(link.getStudentId()).orElse(null);
            if (student != null) {
                students.add(student);
            }
        }
        return students;
    }

    public void removeStudentFromTeacher(String teacherEmail, String studentEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with email: " + teacherEmail);
        }

        User student = userRepository.findByEmail(studentEmail);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with email: " + studentEmail);
        }

        // Find and delete the teacher-student relationship
        TeacherStudent teacherStudent = teacherStudentRepository.findByTeacherIdAndStudentId(teacher.getId(), student.getId());
        if (teacherStudent != null) {
            teacherStudentRepository.delete(teacherStudent);
        } else {
            throw new IllegalArgumentException("Student not associated with this teacher.");
        }
    }


}

