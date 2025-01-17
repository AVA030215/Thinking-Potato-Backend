package com.example.ThinkingPotato.repository;

import com.example.ThinkingPotato.entity.TeacherStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherStudentRepository extends JpaRepository<TeacherStudent, Long> {
    List<TeacherStudent> findByTeacherId(Long teacherId);
    TeacherStudent findByTeacherIdAndStudentId(Long teacherId, Long studentId);
}


