package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeRepo extends JpaRepository<Fee, Long> {

    List<Fee> findByStudent_Id(Long studentId);

    List<Fee> findByStudent_User_Email(String email);

    boolean existsByStudent_IdAndSemester(Long studentId, String semester);

    List<Fee> findByStudent_User_EmailAndSemesterIgnoreCase(String email, String semester);

    Optional<Fee> findByStudent_IdAndSemesterIgnoreCase(Long studentId, String semester);

    void deleteByStudent_Id(Long studentId);
}