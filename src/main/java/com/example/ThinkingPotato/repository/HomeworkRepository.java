package com.example.ThinkingPotato.repository;


import com.example.ThinkingPotato.entity.User;
import com.example.ThinkingPotato.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByStudentId(Long studentId);
    List<Homework> findByTeacherIdAndStudentId(Long teacherId, Long studentId);
    List<Homework> findByTeacherAndStudent(User teacher, User student);

}




