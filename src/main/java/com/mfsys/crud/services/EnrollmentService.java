//
//package com.mfsys.crud.services;
//
//import com.mfsys.crud.Entity.Course;
//import com.mfsys.crud.Entity.Enrollment;
//import com.mfsys.crud.Entity.Student;
//import com.mfsys.crud.Repository.CourseRepo;
//import com.mfsys.crud.Repository.EnrollmentRepo;
//import com.mfsys.crud.Repository.StudentRepo;
//import com.mfsys.crud.dto.EnrollmentResponseDto;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//public class EnrollmentService {
//
//    private static final int MAX_CREDIT_HOURS = 18;
//
//    private final EnrollmentRepo enrollmentRepo;
//    private final StudentRepo studentRepo;
//    private final CourseRepo courseRepo;
//
//    public EnrollmentService(
//            EnrollmentRepo enrollmentRepo,
//            StudentRepo studentRepo,
//            CourseRepo courseRepo
//    ) {
//        this.enrollmentRepo = enrollmentRepo;
//        this.studentRepo = studentRepo;
//        this.courseRepo = courseRepo;
//    }
//
//    @Transactional
//    public EnrollmentResponseDto enrollStudent(
//            Long studentId,
//            Long courseId,
//            String semester
//    ) {
//        if (semester == null || semester.isBlank()) {
//            throw new IllegalArgumentException(
//                    "Semester is required"
//            );
//        }
//
//        semester = semester.trim();
//
//        Student student = studentRepo.findById(studentId)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        Course course = courseRepo.findById(courseId)
//                .orElseThrow(() ->
//                        new RuntimeException("Course not found")
//                );
//
//        if (!"ACTIVE".equalsIgnoreCase(course.getStatus())) {
//            throw new IllegalArgumentException(
//                    "Only ACTIVE courses can be enrolled"
//            );
//        }
//
//        if (course.getCreditHours() <= 0) {
//            throw new IllegalArgumentException(
//                    "Course credit hours must be greater than zero"
//            );
//        }
//
//        boolean alreadyEnrolled =
//                enrollmentRepo
//                        .existsByStudent_IdAndCourse_IdAndSemester(
//                                studentId,
//                                courseId,
//                                semester
//                        );
//
//        if (alreadyEnrolled) {
//            throw new IllegalArgumentException(
//                    "Student is already enrolled in this course for "
//                            + semester
//            );
//        }
//
//        int currentCreditHours =
//                getCreditHoursForSemester(studentId, semester);
//
//        if (currentCreditHours + course.getCreditHours()
//                > MAX_CREDIT_HOURS) {
//            throw new IllegalArgumentException(
//                    "Enrollment exceeds the 18 credit-hour limit for "
//                            + semester
//                            + ". Current credit hours: "
//                            + currentCreditHours
//                            + ", requested: "
//                            + course.getCreditHours()
//            );
//        }
//
//        Enrollment enrollment = new Enrollment();
//        enrollment.setStudent(student);
//        enrollment.setCourse(course);
//        enrollment.setSemester(semester);
//
//        return convertToDto(enrollmentRepo.save(enrollment));
//    }
//
//    public int getCreditHoursForSemester(
//            Long studentId,
//            String semester
//    ) {
//        return enrollmentRepo
//                .findByStudent_IdAndSemester(studentId, semester)
//                .stream()
//                .mapToInt(e -> e.getCourse().getCreditHours())
//                .sum();
//    }
//
//    public List<EnrollmentResponseDto> getStudentEnrollments(
//            Long studentId
//    ) {
//        return enrollmentRepo.findByStudent_Id(studentId)
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    public List<EnrollmentResponseDto>
//    getStudentEnrollmentsBySemester(
//            Long studentId,
//            String semester
//    ) {
//        if (semester == null || semester.isBlank()) {
//            throw new IllegalArgumentException(
//                    "Semester is required"
//            );
//        }
//
//        return enrollmentRepo
//                .findByStudent_IdAndSemester(
//                        studentId, semester.trim()
//                )
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    @Transactional
//    public void removeEnrollment(Long enrollmentId) {
//        if (!enrollmentRepo.existsById(enrollmentId)) {
//            throw new RuntimeException("Enrollment not found");
//        }
//
//        enrollmentRepo.deleteById(enrollmentId);
//    }
//
//
//    public List<EnrollmentResponseDto> getMyEnrollments(String email) {
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student record not found"));
//
//        return enrollmentRepo.findByStudent_Id(student.getId())
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//    public List<EnrollmentResponseDto> getMyEnrollmentsBySemester(
//            String email, String semester) {
//
//        if (semester == null || semester.isBlank()) {
//            throw new IllegalArgumentException("Semester is required");
//        }
//
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student record not found"));
//
//        return enrollmentRepo
//                .findByStudent_IdAndSemester(
//                        student.getId(), semester.trim())
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//
//    @Transactional
//    public EnrollmentResponseDto enrollMyself(
//            String email,
//            Long courseId,
//            String semester
//    ) {
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student record not found")
//                );
//
//        return enrollStudent(student.getId(), courseId, semester);
//    }
//
//    public List<String> getMySemesters(String email) {
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student record not found")
//                );
//
//        return enrollmentRepo.findByStudent_Id(student.getId())
//                .stream()
//                .map(Enrollment::getSemester)
//                .filter(s -> s != null && !s.isBlank())
//                .distinct()
//                .sorted()
//                .toList();
//    }
//
//
//    private EnrollmentResponseDto convertToDto(
//            Enrollment enrollment
//    ) {
//        EnrollmentResponseDto dto =
//                new EnrollmentResponseDto();
//
//        dto.setId(enrollment.getId());
//        dto.setStudentId(enrollment.getStudent().getId());
//        dto.setCourseId(enrollment.getCourse().getId());
//        dto.setCourseTitle(enrollment.getCourse().getTitle());
//        dto.setCourseCode(enrollment.getCourse().getCourseCode());
//        dto.setCreditHours(
//                enrollment.getCourse().getCreditHours()
//        );
//        dto.setCourseStatus(
//                enrollment.getCourse().getStatus()
//        );
//        dto.setSemester(enrollment.getSemester());
//
//        return dto;
//    }
//}





package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Enrollment;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.EnrollmentRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.EnrollmentResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private static final int MAX_CREDIT_HOURS = 18;

    private final EnrollmentRepo enrollmentRepo;
    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;
    private final FeeService feeService;

    public EnrollmentService(
            EnrollmentRepo enrollmentRepo,
            StudentRepo studentRepo,
            CourseRepo courseRepo,
            FeeService feeService
    ) {
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.feeService = feeService;
    }

    @Transactional
    public EnrollmentResponseDto enrollStudent(Long studentId, Long courseId, String semester) {
        if (semester == null || semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }

        semester = semester.trim();

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!"ACTIVE".equalsIgnoreCase(course.getStatus())) {
            throw new IllegalArgumentException("Only ACTIVE courses can be enrolled");
        }

        if (course.getCreditHours() <= 0) {
            throw new IllegalArgumentException("Course credit hours must be greater than zero");
        }

        if (enrollmentRepo.existsByStudent_IdAndCourse_IdAndSemester(studentId, courseId, semester)) {
            throw new IllegalArgumentException(
                    "Student is already enrolled in this course for " + semester);
        }

        int currentCreditHours = getCreditHoursForSemester(studentId, semester);

        if (currentCreditHours + course.getCreditHours() > MAX_CREDIT_HOURS) {
            throw new IllegalArgumentException(
                    "Enrollment exceeds the 18 credit-hour limit for " + semester
                            + ". Current credit hours: " + currentCreditHours
                            + ", requested: " + course.getCreditHours());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(semester);

        Enrollment saved = enrollmentRepo.save(enrollment);

        // keep the fee in line with the new enrollment
        feeService.syncFeeForSemester(studentId, semester);

        return convertToDto(saved);
    }

    public int getCreditHoursForSemester(Long studentId, String semester) {
        return enrollmentRepo
                .findByStudent_IdAndSemester(studentId, semester)
                .stream()
                .mapToInt(e -> e.getCourse().getCreditHours())
                .sum();
    }

    public List<EnrollmentResponseDto> getStudentEnrollments(Long studentId) {
        return enrollmentRepo.findByStudent_Id(studentId)
                .stream().map(this::convertToDto).toList();
    }

    public List<EnrollmentResponseDto> getStudentEnrollmentsBySemester(Long studentId, String semester) {
        if (semester == null || semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }

        return enrollmentRepo
                .findByStudent_IdAndSemester(studentId, semester.trim())
                .stream().map(this::convertToDto).toList();
    }

    @Transactional
    public void removeEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Long studentId = enrollment.getStudent().getId();
        String semester = enrollment.getSemester();

        enrollmentRepo.delete(enrollment);
        enrollmentRepo.flush();

        feeService.syncFeeForSemester(studentId, semester);
    }

    public List<EnrollmentResponseDto> getMyEnrollments(String email) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        return enrollmentRepo.findByStudent_Id(student.getId())
                .stream().map(this::convertToDto).toList();
    }

    public List<EnrollmentResponseDto> getMyEnrollmentsBySemester(String email, String semester) {
        if (semester == null || semester.isBlank()) {
            throw new IllegalArgumentException("Semester is required");
        }

        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        return enrollmentRepo
                .findByStudent_IdAndSemester(student.getId(), semester.trim())
                .stream().map(this::convertToDto).toList();
    }

    @Transactional
    public EnrollmentResponseDto enrollMyself(String email, Long courseId, String semester) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        return enrollStudent(student.getId(), courseId, semester);
    }

    public List<String> getMySemesters(String email) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student record not found"));

        return enrollmentRepo.findByStudent_Id(student.getId())
                .stream()
                .map(Enrollment::getSemester)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    private EnrollmentResponseDto convertToDto(Enrollment enrollment) {
        EnrollmentResponseDto dto = new EnrollmentResponseDto();

        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudent().getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseTitle(enrollment.getCourse().getTitle());
        dto.setCourseCode(enrollment.getCourse().getCourseCode());
        dto.setCreditHours(enrollment.getCourse().getCreditHours());
        dto.setCourseStatus(enrollment.getCourse().getStatus());
        dto.setSemester(enrollment.getSemester());

        return dto;
    }
}