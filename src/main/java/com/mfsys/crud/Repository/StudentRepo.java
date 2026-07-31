package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository<Student, Long> {
}
