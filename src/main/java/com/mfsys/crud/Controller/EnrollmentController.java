package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.EnrollmentResponseDto;
import com.mfsys.crud.services.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // ADMIN: enroll a student
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponseDto> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam String semester) {

        return ResponseEntity.ok(
                enrollmentService.enrollStudent(
                        studentId, courseId, semester));
    }

    // ADMIN: view any student's enrollments
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDto>> getStudentEnrollments(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                enrollmentService.getStudentEnrollments(studentId));
    }

    // ADMIN: view enrollments for a specific semester
    @GetMapping("/student/{studentId}/semester")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDto>>
    getStudentEnrollmentsBySemester(
            @PathVariable Long studentId,
            @RequestParam String semester) {

        return ResponseEntity.ok(
                enrollmentService.getStudentEnrollmentsBySemester(
                        studentId, semester));
    }

    // STUDENT: view their own enrollments
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponseDto>> getMyEnrollments(
            Authentication authentication) {

        return ResponseEntity.ok(
                enrollmentService.getMyEnrollments(
                        authentication.getName()));
    }

    // STUDENT: view their own enrollments for one semester
    @GetMapping("/me/semester")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponseDto>>
    getMyEnrollmentsBySemester(
            Authentication authentication,
            @RequestParam String semester) {

        return ResponseEntity.ok(
                enrollmentService.getMyEnrollmentsBySemester(
                        authentication.getName(), semester));
    }

    // ADMIN: remove an enrollment
    @DeleteMapping("/{enrollmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeEnrollment(
            @PathVariable Long enrollmentId) {

        enrollmentService.removeEnrollment(enrollmentId);
        return ResponseEntity.ok("Enrollment removed successfully");
    }

    // STUDENT: enroll themselves in a selected semester
    @PostMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponseDto> enrollMyself(
            Authentication authentication,
            @RequestParam Long courseId,
            @RequestParam String semester
    ) {
        return ResponseEntity.ok(
                enrollmentService.enrollMyself(
                        authentication.getName(),
                        courseId,
                        semester
                )
        );
    }

    // STUDENT: get their available enrollment semesters
    @GetMapping("/me/semesters")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<String>> getMySemesters(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                enrollmentService.getMySemesters(
                        authentication.getName()
                )
        );
    }

}