//package com.mfsys.crud.services;
//
//import com.mfsys.crud.Entity.Course;
//import com.mfsys.crud.Entity.Student;
//import com.mfsys.crud.Entity.User;
//
//import org.springframework.transaction.annotation.Transactional;
//
//import com.mfsys.crud.Repository.CourseRepo;
//import com.mfsys.crud.Repository.FeeRepo;
//import com.mfsys.crud.Repository.StudentRepo;
//import com.mfsys.crud.Repository.UserRepository;
//
//import com.mfsys.crud.dto.StudentResponseDto;
//import com.mfsys.crud.dto.UpdateProfileRequest;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import org.springframework.stereotype.Service;
//
//import org.springframework.web.multipart.MultipartFile;
//import com.mfsys.crud.Entity.Enrollment;
//import com.mfsys.crud.Repository.EnrollmentRepo;
//import java.util.List;
//
//@Service
//public class StudentsService {
//
//    private final StudentRepo studentRepo;
//    private final CourseRepo courseRepo;
//    private final FeeRepo feeRepo;
//    private final EnrollmentRepo enrollmentRepo;
//
//
//    private static final int MAX_CREDIT_HOURS = 18;
//
//    private static final int TOTAL_SEMESTERS = 8;
//
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//    public StudentsService(
//            StudentRepo studentRepo,
//            CourseRepo courseRepo, FeeRepo feeRepo,EnrollmentRepo enrollmentRepo
//    ) {
//
//        this.studentRepo = studentRepo;
//        this.courseRepo = courseRepo;
//        this.feeRepo = feeRepo;
//        this.enrollmentRepo = enrollmentRepo;
//    }
//
//
//    // =========================================================
//    // GET ALL STUDENTS
//    // =========================================================
//
//    public List<StudentResponseDto> getAllStudents() {
//
//        return studentRepo.findAll()
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//
//    // =========================================================
//    // PAGINATED STUDENTS
//    // =========================================================
//
//    public Page<StudentResponseDto> getAllStudents(
//            Pageable pageable
//    ) {
//
//        return studentRepo
//                .findAll(pageable)
//                .map(this::convertToDto);
//    }
//
//
//    // =========================================================
//    // GET STUDENT BY ID
//    // =========================================================
//
//    public StudentResponseDto getStudentById(
//            int id
//    ) {
//
//        Student student =
//                studentRepo
//                        .findById((long) id)
//                        .orElse(null);
//
//        if (student == null) {
//            return null;
//        }
//
//        return convertToDto(student);
//    }
//
//
//    // =========================================================
//    // GET STUDENT BY EMAIL
//    // =========================================================
//
//    public StudentResponseDto getStudentByEmail(
//            String email
//    ) {
//
//        Student student =
//                studentRepo
//                        .findByUser_Email(email)
//                        .orElse(null);
//
//        if (student == null) {
//            return null;
//        }
//
//        return convertToDto(student);
//    }
//
//
//    // =========================================================
//// DELETE STUDENT
//// =========================================================
//    @Transactional
//    public void deleteStudent(int id) {
//        Student student = studentRepo.findById((long) id)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        User user = student.getUser();
//
//        feeRepo.deleteByStudent_Id(student.getId());
//        enrollmentRepo.deleteByStudent_Id(student.getId());
//
//        studentRepo.delete(student);
//
//        if (user != null) {
//            userRepository.delete(user);
//        }
//    }
//    // =========================================================
//    // ASSIGN COURSE
//    // =========================================================
//
//
//
//
//    // =========================================================
//    // UPDATE PROFILE
//    // =========================================================
//
//    public StudentResponseDto updateMyProfile(
//            String email,
//            UpdateProfileRequest request
//    ) {
//
//        Student student =
//                studentRepo
//                        .findByUser_Email(email)
//                        .orElseThrow(
//                                () -> new RuntimeException(
//                                        "Student not found"
//                                )
//                        );
//
//        User user = student.getUser();
//
//        user.setUsername(request.getUsername());
//
//        user.setFullName(request.getFullName());
//
//        user.setPhone(request.getPhone());
//
//        userRepository.save(user);
//
//        return convertToDto(student);
//    }
//
//    public StudentResponseDto assignCourse(
//            int studentId,
//            Long courseId
//    ) {
//        Student student = studentRepo.findById((long) studentId)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        String semester = calculateCurrentSemester(
//                student.getUser().getAdmissionYear()
//        )[0];
//
//        enrollmentServiceForLegacy(
//                student.getId(),
//                courseId,
//                semester
//        );
//
//        return convertToDto(student);
//    }
//
//    public StudentResponseDto enrollInCourse(
//            String email,
//            Long courseId
//    ) {
//        Student student = studentRepo.findByUser_Email(email)
//                .orElseThrow(() ->
//                        new RuntimeException("Student not found")
//                );
//
//        String semester = calculateCurrentSemester(
//                student.getUser().getAdmissionYear()
//        )[0];
//
//        enrollmentServiceForLegacy(
//                student.getId(),
//                courseId,
//                semester
//        );
//
//        return convertToDto(student);
//    }
//
//    private void enrollmentServiceForLegacy(
//            Long studentId,
//            Long courseId,
//            String semester
//    ) {
//        Course course = courseRepo.findById(courseId)
//                .orElseThrow(() ->
//                        new RuntimeException("Course not found")
//                );
//
//        if (!"ACTIVE".equalsIgnoreCase(course.getStatus())) {
//            throw new RuntimeException(
//                    "Only ACTIVE courses can be enrolled"
//            );
//        }
//
//        boolean alreadyEnrolled =
//                enrollmentRepo
//                        .existsByStudent_IdAndCourse_IdAndSemester(
//                                studentId, courseId, semester
//                        );
//
//        if (alreadyEnrolled) {
//            throw new RuntimeException(
//                    "Student is already enrolled in this course for "
//                            + semester
//            );
//        }
//
//        int currentHours = enrollmentRepo
//                .findByStudent_IdAndSemester(studentId, semester)
//                .stream()
//                .mapToInt(e -> e.getCourse().getCreditHours())
//                .sum();
//
//        if (currentHours + course.getCreditHours() > MAX_CREDIT_HOURS) {
//            throw new RuntimeException(
//                    "Maximum 18 credit hours allowed per semester"
//            );
//        }
//
//        Enrollment enrollment = new Enrollment();
//
//        enrollment.setStudent(
//                studentRepo.findById(studentId)
//                        .orElseThrow(() ->
//                                new RuntimeException("Student not found")
//                        )
//        );
//        enrollment.setCourse(course);
//        enrollment.setSemester(semester);
//
//        enrollmentRepo.save(enrollment);
//    }
//
//    private int getTotalCreditHours(Student student) {
//        String semester = calculateCurrentSemester(
//                student.getUser().getAdmissionYear()
//        )[0];
//
//        return enrollmentRepo
//                .findByStudent_IdAndSemester(
//                        student.getId(), semester
//                )
//                .stream()
//                .mapToInt(e -> e.getCourse().getCreditHours())
//                .sum();
//    }
//
//
//    // =========================================================
//    // SEARCH
//    // =========================================================
//
//    public List<StudentResponseDto> searchStudentsByName(
//            String pattern
//    ) {
//
//        if (
//                pattern == null
//                        || pattern.isBlank()
//        ) {
//
//            return List.of();
//        }
//
//        return studentRepo
//                .searchByUsernameRegex(pattern)
//                .stream()
//                .map(this::convertToDto)
//                .toList();
//    }
//
//
//    // =========================================================
//    // SEMESTER CALCULATION
//    // =========================================================
//
//    private String[] calculateCurrentSemester(
//            Integer admissionYear
//    ) {
//
//        if (admissionYear == null) {
//
//            return new String[]{
//                    "—",
//                    "0"
//            };
//        }
//
//        List<String> semesterLabels =
//                new java.util.ArrayList<>();
//
//        for (int i = 0; i < 4; i++) {
//
//            int year = admissionYear + i;
//
//            semesterLabels.add(
//                    "Fall " + year
//            );
//
//            semesterLabels.add(
//                    "Summer " + year
//            );
//        }
//
//        java.time.LocalDate admissionStart =
//                java.time.LocalDate.of(
//                        admissionYear,
//                        9,
//                        1
//                );
//
//        java.time.LocalDate today =
//                java.time.LocalDate.now();
//
//        long monthsElapsed =
//                java.time.temporal.ChronoUnit.MONTHS
//                        .between(
//                                admissionStart,
//                                today
//                        );
//
//        int semesterIndex =
//                (int) (monthsElapsed / 6);
//
//        if (semesterIndex < 0) {
//            semesterIndex = 0;
//        }
//
//        if (semesterIndex > 7) {
//            semesterIndex = 7;
//        }
//
//        return new String[]{
//                semesterLabels.get(semesterIndex),
//                String.valueOf(
//                        semesterIndex + 1
//                )
//        };
//    }
//
//
//    // =========================================================
//    // TOTAL STUDENTS
//    // =========================================================
//
//    public long getTotalStudentsCount() {
//
//        return studentRepo.count();
//    }
//
//
//    // =========================================================
//    // CONVERT ENTITY TO DTO
//    // =========================================================
//
//    private StudentResponseDto convertToDto(
//            Student student
//    ) {
//
//        String currentSemester = calculateCurrentSemester(
//                student.getUser().getAdmissionYear()
//        )[0];
//
//        List<String> courseTitles = enrollmentRepo
//                .findByStudent_IdAndSemester(
//                        student.getId(), currentSemester
//                )
//                .stream()
//                .map(Enrollment::getCourse)
//                .map(Course::getTitle)
//                .distinct()
//                .toList();
//
//        String[] semesterInfo =
//                calculateCurrentSemester(
//                        student
//                                .getUser()
//                                .getAdmissionYear()
//                );
//
//        int enrolledCoursesCount =
//                courseTitles.size();
//
//        long availableCoursesCount =
//                courseRepo.countByDepartment_Id(
//                        student
//                                .getUser()
//                                .getDepartment()
//                                .getId()
//                );
//
//        return new StudentResponseDto(
//
//                student.getId(),
//
//                student.getUser().getFullName(),
//
//                student.getUser().getPhone(),
//
//                student.getUser().getUsername(),
//
//                student.getUser().getEmail(),
//
//                student
//                        .getUser()
//                        .getDepartment()
//                        .getName(),
//
//                student
//                        .getUser()
//                        .getDepartment()
//                        .getId(),
//
//                courseTitles,
//
//                getTotalCreditHours(student),
//
//                MAX_CREDIT_HOURS,
//
//                student
//                        .getUser()
//                        .getProfilePicture(),
//
//                student
//                        .getUser()
//                        .getAdmissionYear(),
//
//                semesterInfo[0],
//
//                Integer.parseInt(
//                        semesterInfo[1]
//                ),
//
//                TOTAL_SEMESTERS,
//
//                enrolledCoursesCount,
//
//                (int) availableCoursesCount
//        );
//    }
//
//
//    // =========================================================
//    // PROFILE PICTURE
//    // =========================================================
//
//    public String uploadProfilePicture(
//            String email,
//            MultipartFile file
//    ) throws java.io.IOException {
//
//        User user =
//                userRepository
//                        .findByEmail(email)
//                        .orElseThrow(
//                                () -> new RuntimeException(
//                                        "User not found"
//                                )
//                        );
//
//        String uploadDir =
//                "uploads/profile-pictures/";
//
//        java.io.File directory =
//                new java.io.File(uploadDir);
//
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }
//
//        String fileName =
//                System.currentTimeMillis()
//                        + "_"
//                        + file.getOriginalFilename();
//
//        java.nio.file.Path filePath =
//                java.nio.file.Paths.get(
//                        uploadDir + fileName
//                );
//
//        java.nio.file.Files.write(
//                filePath,
//                file.getBytes()
//        );
//
//        user.setProfilePicture(fileName);
//
//        userRepository.save(user);
//
//        return fileName;
//    }
//}








package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Enrollment;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.EnrollmentRepo;
import com.mfsys.crud.Repository.FeeRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.Repository.UserRepository;
import com.mfsys.crud.dto.StudentResponseDto;
import com.mfsys.crud.dto.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StudentsService {

    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;
    private final FeeRepo feeRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final EnrollmentService enrollmentService;

    private static final int MAX_CREDIT_HOURS = 18;
    private static final int TOTAL_SEMESTERS = 8;

    @Autowired
    private UserRepository userRepository;

    public StudentsService(
            StudentRepo studentRepo,
            CourseRepo courseRepo,
            FeeRepo feeRepo,
            EnrollmentRepo enrollmentRepo,
            EnrollmentService enrollmentService
    ) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.feeRepo = feeRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.enrollmentService = enrollmentService;
    }

    public List<StudentResponseDto> getAllStudents() {
        return studentRepo.findAll().stream().map(this::convertToDto).toList();
    }

    public Page<StudentResponseDto> getAllStudents(Pageable pageable) {
        return studentRepo.findAll(pageable).map(this::convertToDto);
    }

    public StudentResponseDto getStudentById(int id) {
        Student student = studentRepo.findById((long) id).orElse(null);
        return student == null ? null : convertToDto(student);
    }

    public StudentResponseDto getStudentByEmail(String email) {
        Student student = studentRepo.findByUser_Email(email).orElse(null);
        return student == null ? null : convertToDto(student);
    }

    @Transactional
    public void deleteStudent(int id) {
        Student student = studentRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User user = student.getUser();

        feeRepo.deleteByStudent_Id(student.getId());
        enrollmentRepo.deleteByStudent_Id(student.getId());

        studentRepo.delete(student);

        if (user != null) {
            userRepository.delete(user);
        }
    }

    public StudentResponseDto updateMyProfile(String email, UpdateProfileRequest request) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        User user = student.getUser();
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        return convertToDto(student);
    }

    // Legacy endpoints: now enroll through EnrollmentService (fee sync included)
    @Transactional
    public StudentResponseDto assignCourse(int studentId, Long courseId) {
        Student student = studentRepo.findById((long) studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String semester = calculateCurrentSemester(
                student.getUser().getAdmissionYear())[0];

        enrollmentService.enrollStudent(student.getId(), courseId, semester);

        return convertToDto(student);
    }

    @Transactional
    public StudentResponseDto enrollInCourse(String email, Long courseId) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String semester = calculateCurrentSemester(
                student.getUser().getAdmissionYear())[0];

        enrollmentService.enrollStudent(student.getId(), courseId, semester);

        return convertToDto(student);
    }

    private int getTotalCreditHours(Student student) {
        String semester = calculateCurrentSemester(
                student.getUser().getAdmissionYear())[0];

        return enrollmentRepo
                .findByStudent_IdAndSemester(student.getId(), semester)
                .stream()
                .mapToInt(e -> e.getCourse().getCreditHours())
                .sum();
    }

    public List<StudentResponseDto> searchStudentsByName(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return List.of();
        }

        return studentRepo.searchByUsernameRegex(pattern)
                .stream().map(this::convertToDto).toList();
    }

    private String[] calculateCurrentSemester(Integer admissionYear) {
        if (admissionYear == null) {
            return new String[]{"—", "0"};
        }

        List<String> semesterLabels = new java.util.ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int year = admissionYear + i;
            semesterLabels.add("Fall " + year);
            semesterLabels.add("Summer " + year);
        }

        java.time.LocalDate admissionStart = java.time.LocalDate.of(admissionYear, 9, 1);
        java.time.LocalDate today = java.time.LocalDate.now();

        long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS
                .between(admissionStart, today);

        int semesterIndex = (int) (monthsElapsed / 6);

        if (semesterIndex < 0) semesterIndex = 0;
        if (semesterIndex > 7) semesterIndex = 7;

        return new String[]{
                semesterLabels.get(semesterIndex),
                String.valueOf(semesterIndex + 1)
        };
    }

    public long getTotalStudentsCount() {
        return studentRepo.count();
    }

    private StudentResponseDto convertToDto(Student student) {
        String[] semesterInfo = calculateCurrentSemester(
                student.getUser().getAdmissionYear());

        List<String> courseTitles = enrollmentRepo
                .findByStudent_IdAndSemester(student.getId(), semesterInfo[0])
                .stream()
                .map(Enrollment::getCourse)
                .map(Course::getTitle)
                .distinct()
                .toList();

        long availableCoursesCount = courseRepo.countByDepartment_Id(
                student.getUser().getDepartment().getId());

        return new StudentResponseDto(
                student.getId(),
                student.getUser().getFullName(),
                student.getUser().getPhone(),
                student.getUser().getUsername(),
                student.getUser().getEmail(),
                student.getUser().getDepartment().getName(),
                student.getUser().getDepartment().getId(),
                courseTitles,
                getTotalCreditHours(student),
                MAX_CREDIT_HOURS,
                student.getUser().getProfilePicture(),
                student.getUser().getAdmissionYear(),
                semesterInfo[0],
                Integer.parseInt(semesterInfo[1]),
                TOTAL_SEMESTERS,
                courseTitles.size(),
                (int) availableCoursesCount
        );
    }

    public String uploadProfilePicture(String email, MultipartFile file)
            throws java.io.IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String uploadDir = "uploads/profile-pictures/";
        java.io.File directory = new java.io.File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        java.nio.file.Files.write(
                java.nio.file.Paths.get(uploadDir + fileName),
                file.getBytes()
        );

        user.setProfilePicture(fileName);
        userRepository.save(user);

        return fileName;
    }
}