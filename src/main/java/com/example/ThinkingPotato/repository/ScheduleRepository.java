package com.example.ThinkingPotato.repository;

import com.example.ThinkingPotato.entity.Schedule;
import com.example.ThinkingPotato.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByTeacher(User teacher);
    List<Schedule> findByStudent(User student);

    @Query("SELECT s FROM Schedule s WHERE s.teacher = :teacher AND s.startDate BETWEEN :startDate AND :endDate")
    List<Schedule> findByTeacherAndStartDateBetween(
            @Param("teacher") User teacher,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ Custom Query to Delete All Future Occurrences
    @Modifying
    @Transactional
    @Query("DELETE FROM Schedule s WHERE s.teacher = :teacher AND s.startDate >= :startDate")
    void deleteByTeacherAndStartDate(@Param("teacher") User teacher, @Param("startDate") LocalDate startDate);

}


