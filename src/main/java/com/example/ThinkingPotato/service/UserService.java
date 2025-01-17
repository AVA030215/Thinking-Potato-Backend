package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.repository.UserRepository;
import com.example.ThinkingPotato.repository.TeacherStudentRepository;
import com.example.ThinkingPotato.entity.TeacherStudent;
import com.example.ThinkingPotato.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private TeacherStudentRepository teacherStudentRepository;

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        System.out.println("Looking for email: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return user;
    }


    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getStudentsForTeacherDetails(String teacherEmail) {
        // Find teacher by email
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with email: " + teacherEmail);
        }

        // Get list of TeacherStudent mappings by teacherId
        List<TeacherStudent> teacherStudentMappings = teacherStudentRepository.findByTeacherId(teacher.getId());

        // Fetch student details using student IDs
        List<User> students = teacherStudentMappings.stream()
                .map(mapping -> userRepository.findById(mapping.getStudentId())
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return students;
    }

}
