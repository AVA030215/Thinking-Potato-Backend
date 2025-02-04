package com.example.ThinkingPotato.repository;

import com.example.ThinkingPotato.entity.TeacherStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface TeacherStudentRepository extends JpaRepository<TeacherStudent, Long> {
    List<TeacherStudent> findByTeacherId(Long teacherId);

    TeacherStudent findByTeacherIdAndStudentId(Long teacherId, Long studentId);

    boolean existsByTeacherIdAndStudentId(Long teacherId, Long studentId);

    @Query("SELECT ts FROM TeacherStudent ts WHERE ts.teacherId = :teacherId AND ts.studentId = :studentId")
    List<TeacherStudent> findByTeacherAndStudent(@Param("teacherId") Long teacherId, @Param("studentId") Long studentId);

    // ✅ Ensure this returns the correct teacher_student.id
    // Now, the teacherId is always 14. Has to be update when the teachers increase
    @Query("SELECT ts.id FROM TeacherStudent ts WHERE ts.studentId = :studentId AND ts.teacherId = 14")
    Optional<Long> findTeacherStudentIdByStudentId(@Param("studentId") Long studentId);



}


