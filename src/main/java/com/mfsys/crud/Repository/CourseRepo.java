package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CourseRepo extends JpaRepository<Course,Long> {
    List<Course> findByDepartment_Id(Long departmentId);
    long countByDepartment_Id(Long departmentId);
}
