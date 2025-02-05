package com.example.ThinkingPotato.service;

import com.example.ThinkingPotato.dto.UserResponse;
import com.example.ThinkingPotato.repository.UserRepository;
import com.example.ThinkingPotato.repository.TeacherStudentRepository;
import com.example.ThinkingPotato.entity.TeacherStudent;
import com.example.ThinkingPotato.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;


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

        if (user.getProfilePhoto() == null || user.getProfilePhoto().isEmpty()) {
            user.setProfilePhoto("/pubilc/img/profile/justpotato.png");
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

    public boolean isStudentAlreadyAdded(String teacherEmail, String studentEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        User student = userRepository.findByEmail(studentEmail);

        if (teacher == null || student == null) {
            return false;
        }

        return teacherStudentRepository.existsByTeacherIdAndStudentId(teacher.getId(), student.getId());
    }

    public List<UserResponse> getStudentsForTeacherDetails(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found with email: " + teacherEmail);
        }

        List<TeacherStudent> teacherStudentMappings = teacherStudentRepository.findByTeacherId(teacher.getId());

        // ✅ Use HashSet to prevent duplicates
        Set<Long> uniqueStudentIds = new HashSet<>();

        return teacherStudentMappings.stream()
                .map(mapping -> userRepository.findById(mapping.getStudentId())
                        .map(student -> {
                            if (!uniqueStudentIds.contains(student.getId())) {  // ✅ Check for duplicates
                                uniqueStudentIds.add(student.getId());  // ✅ Add unique student
                                return new UserResponse(
                                        student.getId(),  // ✅ Include ID
                                        student.getFirstName(),
                                        student.getLastName(),
                                        student.getEmail(),
                                        student.getMathLevel(),
                                        student.getColorCode().startsWith("#") ? student.getColorCode() : "#" + student.getColorCode()  // ✅ Include Color
                                );
                            }
                            return null; // Skip duplicate
                        })
                        .orElse(null))
                .filter(Objects::nonNull)  // ✅ Remove nulls
                .collect(Collectors.toList());
    }


    // Assigns a unique color for each student
    private String getColorForStudent(Long studentId) {
        String[] colors = {"#ff6666", "#66b3ff", "#99ff99", "#ffcc99", "#c299ff"};
        return colors[(int) (studentId % colors.length)];
    }



    public void addStudentToTeacher(String teacherEmail, String studentEmail) {
        if (isStudentAlreadyAdded(teacherEmail, studentEmail)) {
            throw new IllegalArgumentException("This student is already assigned to the teacher.");
        }

        User teacher = userRepository.findOptionalByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with email: " + teacherEmail));

        User student = userRepository.findOptionalByEmail(studentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + studentEmail));

        TeacherStudent teacherStudent = new TeacherStudent();
        teacherStudent.setTeacherId(teacher.getId());
        teacherStudent.setStudentId(student.getId());

        teacherStudentRepository.save(teacherStudent);
    }


    public void removeStudentFromTeacher(String teacherEmail, String studentEmail) {
        User teacher = userRepository.findByEmail(teacherEmail);
        User student = userRepository.findByEmail(studentEmail);

        if (teacher == null || student == null) {
            throw new IllegalArgumentException("Teacher or Student not found.");
        }

        List<TeacherStudent> mappings = teacherStudentRepository.findByTeacherAndStudent(teacher.getId(), student.getId());

        if (mappings.isEmpty()) {
            throw new IllegalArgumentException("No mapping found for this teacher and student.");
        }

        // 🔹 Ensure we delete only ONE instance (avoiding accidental multiple deletions)
        teacherStudentRepository.delete(mappings.get(0));
    }

    // ✅ Get student_id from email
    public Optional<Long> getStudentIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    //profile photo
    public User updateProfilePhoto(String email, String newPhotoUrl) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        user.setProfilePhoto(newPhotoUrl);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user); // ✅ No email duplication check
    }

}



