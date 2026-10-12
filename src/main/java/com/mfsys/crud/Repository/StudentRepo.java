package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_Email(String email);

    @Query(value = "SELECT s.* FROM student s " +
            "JOIN user u ON s.user_id = u.id " +
            "WHERE u.username REGEXP :pattern", nativeQuery = true)
    List<Student> searchByUsernameRegex(@Param("pattern") String pattern);
}