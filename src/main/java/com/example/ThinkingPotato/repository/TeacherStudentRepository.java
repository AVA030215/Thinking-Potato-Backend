package com.example.ThinkingPotato.repository;

import com.example.ThinkingPotato.entity.TeacherStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TeacherStudentRepository extends JpaRepository<TeacherStudent, Long> {
    List<TeacherStudent> findByTeacherId(Long teacherId);
    TeacherStudent findByTeacherIdAndStudentId(Long teacherId, Long studentId);
    boolean existsByTeacherIdAndStudentId(Long teacherId, Long studentId);

    @Query("SELECT ts FROM TeacherStudent ts WHERE ts.teacherId = :teacherId AND ts.studentId = :studentId")
    List<TeacherStudent> findByTeacherAndStudent(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);
}


