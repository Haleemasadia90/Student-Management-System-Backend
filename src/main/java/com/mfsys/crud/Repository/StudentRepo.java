package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_Email(String email);

}
