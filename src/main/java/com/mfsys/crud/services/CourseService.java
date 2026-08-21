package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.CourseRequest;
import com.mfsys.crud.dto.CourseResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class CourseService {
    private final CourseRepo courseRepo;
    private final StudentRepo studentRepo;

    public CourseService(CourseRepo courseRepo, StudentRepo studentRepo)
    {
        this.courseRepo=courseRepo;
        this.studentRepo=studentRepo;
    }
 public List<CourseResponseDto> getAllCourses(){
        return courseRepo.findAll().stream().map(this::convertToDto).toList();
 }

 public CourseResponseDto getCourseById(int id){
        Course course = courseRepo.findById((long) id).orElse(null);
        if(course == null)
            return null;
        return convertToDto(course);
 }

 public CourseResponseDto createCourse(CourseRequest request){
        Course newCourse = new Course();
        newCourse.setTitle(request.getTitle());
        newCourse.setDescription(request.getDescription());
     newCourse.setCourseCode(request.getCourseCode());
     newCourse.setCreditHours(request.getCreditHours());
     newCourse.setStatus(request.getStatus());

        Course saved = courseRepo.save(newCourse);
        return convertToDto(saved);
 }

 public CourseResponseDto updateCourse(int id, CourseRequest request){
        Course course = courseRepo.findById((long) id).orElseThrow(()-> new RuntimeException("Course not found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
     course.setCourseCode(request.getCourseCode());
     course.setCreditHours(request.getCreditHours());
     course.setStatus(request.getStatus());

        Course updated = courseRepo.save(course);
        return convertToDto(updated);
 }

 public void deleteCourse(int id)
 {
     courseRepo.deleteById((long) id);
 }

 public void removeCourseFromStudent(int studentId, Long courseId){
        Student student = studentRepo.findById((long) studentId).orElseThrow(()->new RuntimeException("Stuident not found"));

        Course course = courseRepo.findById(courseId).orElseThrow(()->new RuntimeException("Course not found"));


     student.getCourses().remove(course);
     studentRepo.save(student);

    }

 private CourseResponseDto convertToDto(Course course)
 {
     return new CourseResponseDto(
             course.getId(),
             course.getTitle(),
             course.getDescription(),
             course.getCourseCode(),
             course.getCreditHours(),
             course.getStatus()
     );
 }
}
