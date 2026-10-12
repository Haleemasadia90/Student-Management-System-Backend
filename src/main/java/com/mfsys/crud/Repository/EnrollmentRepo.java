package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent_Id(Long studentId);

    List<Enrollment> findByStudent_IdAndSemester(Long studentId, String semester);

    Optional<Enrollment> findByStudent_IdAndCourse_IdAndSemester(
            Long studentId, Long courseId, String semester);

    boolean existsByStudent_IdAndCourse_IdAndSemester(
            Long studentId, Long courseId, String semester);

    boolean existsByCourse_Id(Long courseId);

    void deleteByStudent_Id(Long studentId);

    void deleteByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}