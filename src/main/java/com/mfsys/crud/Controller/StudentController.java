package com.mfsys.crud.Controller;

import com.mfsys.crud.common.ApiResponse;
import com.mfsys.crud.dto.AssignCourseRequest;
import com.mfsys.crud.dto.StudentResponseDto;
import com.mfsys.crud.dto.UpdateProfileRequest;
import com.mfsys.crud.services.CourseService;
import com.mfsys.crud.services.StudentsService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentsService studentService;
    private final CourseService courseService;

    public StudentController(
            StudentsService studentService,
            CourseService courseService
    ) {
        this.studentService = studentService;
        this.courseService = courseService;
    }


    // =========================================================
    // GET ALL STUDENTS
    // =========================================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponseDto> getAllStudents() {

        return studentService.getAllStudents();
    }


    // =========================================================
    // PAGINATED STUDENTS
    // =========================================================

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<StudentResponseDto>>> getStudents(
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        Page<StudentResponseDto> page =
                studentService.getAllStudents(pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "SUCC_STD_001",
                        "Students fetched successfully",
                        page
                )
        );
    }


    // =========================================================
    // GET STUDENT BY ID
    // =========================================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponseDto getStudentById(
            @PathVariable int id
    ) {

        return studentService.getStudentById(id);
    }


    // =========================================================
    // SEARCH STUDENTS
    // =========================================================

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponseDto> searchStudents(
            @RequestParam String name
    ) {

        return studentService.searchStudentsByName(name);
    }


    // =========================================================
    // DELETE STUDENT
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStudent(
            @PathVariable int id
    ) {

        studentService.deleteStudent(id);
    }


    // =========================================================
    // CURRENT STUDENT
    // =========================================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponseDto getMyRecord(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return studentService.getStudentByEmail(email);
    }


    // =========================================================
    // ASSIGN COURSE
    // =========================================================

    @PostMapping("/{id}/assign-course")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponseDto assignCourse(
            @PathVariable int id,
            @RequestBody AssignCourseRequest request
    ) {

        return studentService.assignCourse(
                id,
                request.getCourseId()
        );
    }


    // =========================================================
    // STUDENT ENROLL COURSE
    // =========================================================

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentResponseDto enrollInCourse(
            Authentication authentication,
            @RequestBody AssignCourseRequest request
    ) {

        String email = authentication.getName();

        return studentService.enrollInCourse(
                email,
                request.getCourseId()
        );
    }


    // =========================================================
    // REMOVE COURSE
    // =========================================================

    @DeleteMapping("/{id}/remove-course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeCourse(
            @PathVariable int id,
            @PathVariable Long courseId
    ) {

        courseService.removeCourseFromStudent(
                id,
                courseId
        );
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    @PutMapping("/update-profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {

        try {

            String email = authentication.getName();

            StudentResponseDto updated =
                    studentService.updateMyProfile(
                            email,
                            request
                    );

            return ResponseEntity.ok(updated);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // UPLOAD PROFILE PHOTO
    // =========================================================

    @PostMapping("/upload-photo")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> uploadPhoto(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {

        try {

            String email = authentication.getName();

            String fileName =
                    studentService.uploadProfilePicture(
                            email,
                            file
                    );

            return ResponseEntity.ok(fileName);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Upload failed: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // GET STUDENT BY EMAIL
    // =========================================================

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponseDto getStudentByEmail(
            @PathVariable String email
    ) {

        return studentService.getStudentByEmail(email);
    }


    // =========================================================
    // TOTAL STUDENTS
    // =========================================================

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public long getTotalStudents() {

        return studentService.getTotalStudentsCount();
    }
}