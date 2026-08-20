package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CourseRepo extends JpaRepository<Course,Long> {
}
