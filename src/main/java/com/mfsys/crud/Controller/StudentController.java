
package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.AssignCourseRequest;
import com.mfsys.crud.dto.StudentResponseDto;
import com.mfsys.crud.dto.UpdateProfileRequest;
import com.mfsys.crud.services.CourseService;
import com.mfsys.crud.services.StudentsService;
//import org.apache.tomcat.util.http.parser.Authorization;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentsService studentService;
    private final CourseService courseService;

    public StudentController(StudentsService studentService, CourseService courseService) {
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponseDto> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponseDto getStudentById(@PathVariable int id) {
        return studentService.getStudentById(id);
    }


    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponseDto> searchStudents(@RequestParam String name) {
        return studentService.searchStudentsByName(name);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponseDto getMyRecord(Authentication authentication)
    {
        String email = authentication.getName();
        return studentService.getStudentByEmail(email);
    }

    @PostMapping("/{id}/assign-course")
    @PreAuthorize("hasRole('ADMIN')")
    public  StudentResponseDto assignCourse(@PathVariable int id, @RequestBody AssignCourseRequest request)
    {
        return studentService.assignCourse(id, request.getCourseId());
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponseDto enrollInCourse(Authentication authentication, @RequestBody AssignCourseRequest request) {
        String email = authentication.getName();
        return studentService.enrollInCourse(email, request.getCourseId());
    }

    @DeleteMapping("/{id}/remove-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeCourse(@PathVariable int id, @PathVariable Long courseId){
        courseService.removeCourseFromStudent(id,courseId);
    }



    @PutMapping("/update-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        try {
            String email = authentication.getName();
            StudentResponseDto updated = studentService.updateMyProfile(email, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-photo")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> uploadPhoto(Authentication authentication, @RequestParam("file") MultipartFile file) {
        try {
            String email = authentication.getName();
            String fileName = studentService.uploadProfilePicture(email, file);
            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponseDto getStudentByEmail(@PathVariable String email) {
        return studentService.getStudentByEmail(email);
    }

//    calculate total students
@GetMapping("/count")
@PreAuthorize("hasRole('ADMIN')")
public long getTotalStudents() {
    return studentService.getTotalStudentsCount();
}
}
