
package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.Repository.UserRepository;
import com.mfsys.crud.dto.StudentResponseDto;
import com.mfsys.crud.dto.UpdateProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StudentsService {
    private final StudentRepo studentRepo;
    private final CourseRepo courseRepo;

    private static final int MAX_CREDIT_HOURS = 18;

    public StudentsService(StudentRepo studentRepo, CourseRepo courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }
    @Autowired
    private  UserRepository userRepository;

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

    public StudentResponseDto enrollInCourse(String email, Long courseId) {
        Student student = studentRepo.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        int currentCreditHours = getTotalCreditHours(student);

        if (currentCreditHours + course.getCreditHours() > MAX_CREDIT_HOURS) {
            throw new RuntimeException(
                    "Enrolling in this course would exceed the " + MAX_CREDIT_HOURS + " credit hour limit. " +
                            "Currently enrolled: " + currentCreditHours + " credit hours."
            );
        }

        student.getCourses().add(course);
        Student updated = studentRepo.save(student);
        return convertToDto(updated);
    }

    private int getTotalCreditHours(Student student) {
        if (student.getCourses() == null) return 0;
        return student.getCourses().stream()
                .mapToInt(Course::getCreditHours)
                .sum();
    }

    public List<StudentResponseDto> searchStudentsByName(String pattern) {
        if (pattern == null || pattern.isBlank()) {
            return List.of();
        }
        return studentRepo.searchByUsernameRegex(pattern)
                .stream()
                .map(this::convertToDto)
                .toList();
    }



//    calculateCurrentSemester
private static final int TOTAL_SEMESTERS = 8;

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

        long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS.between(admissionStart, today);

        int semesterIndex = (int) (monthsElapsed / 6);

        if (semesterIndex < 0) semesterIndex = 0;
        if (semesterIndex > 7) semesterIndex = 7;

        return new String[]{semesterLabels.get(semesterIndex), String.valueOf(semesterIndex + 1)};
    }



//    claculate total students
public long getTotalStudentsCount() {
    return studentRepo.count();
}

    private StudentResponseDto convertToDto(Student student) {
        List<String> courseTitles = student.getCourses() == null
                ? List.of()
                : student.getCourses().stream().map(Course::getTitle).toList();
        String[] semesterInfo = calculateCurrentSemester(student.getUser().getAdmissionYear());


        int enrolledCoursesCount = courseTitles.size();
        long availableCoursesCount = courseRepo.countByDepartment_Id(
                student.getUser().getDepartment().getId()
        );

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
                enrolledCoursesCount,
                (int) availableCoursesCount

        );

    }

    public String uploadProfilePicture(String email, MultipartFile file) throws java.io.IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String uploadDir = "uploads/profile-pictures/";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + fileName);
        java.nio.file.Files.write(filePath, file.getBytes());

        user.setProfilePicture(fileName);
        userRepository.save(user);

        return fileName;
    }
}