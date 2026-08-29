
package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.StudentResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsService {
    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;

    public StudentsService(StudentRepo studentRepo, CourseRepo courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    public List<StudentResponseDto> getAllStudents() {
        return studentRepo.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public StudentResponseDto getStudentById(int id) {
        Student student = studentRepo.findById((long) id).orElse(null);
        if (student == null) return null;
        return convertToDto(student);
    }

    public StudentResponseDto getStudentByEmail(String email)
    {
        Student student = studentRepo.findByUser_Email(email).orElse(null);
    if(student == null) return null;
    return convertToDto(student);
    }

    public void deleteStudent(int id) {
        studentRepo.deleteById((long) id);
    }

    public StudentResponseDto assignCourse(int studentId, Long courseId)
    {
        Student student = studentRepo.findById((long) studentId).orElseThrow(()-> new RuntimeException("Student not found"));

        Course course = courseRepo.findById(courseId).orElseThrow(()->new RuntimeException("Course not found"));

        student.getCourses().add(course);
        Student updated = studentRepo.save(student);

        return convertToDto(updated);
    }

    public StudentResponseDto enrollInCourse(String email, Long courseId) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().add(course);
        Student updated = studentRepo.save(student);
        return convertToDto(updated);
    }

    private StudentResponseDto convertToDto(Student student) {
        List<String> courseTitles = student.getCourses() == null
                ? List.of()
                : student.getCourses().stream().map(Course::getTitle).toList();

        return new StudentResponseDto(
                student.getId(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getUser().getDepartment().getName(),
                student.getUser().getDepartment().getId(),
                courseTitles
        );
    }
}