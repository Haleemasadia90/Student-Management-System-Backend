

package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Course;
import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Entity.Enrollment;
import com.mfsys.crud.Repository.CourseRepo;
import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.Repository.EnrollmentRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.dto.CourseRequest;
import com.mfsys.crud.dto.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepo courseRepo;
    private final StudentRepo studentRepo;
    private final DepartmentRepo departmentRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final FeeService feeService;

    public CourseService(
            CourseRepo courseRepo,
            StudentRepo studentRepo,
            DepartmentRepo departmentRepo,
            EnrollmentRepo enrollmentRepo,
            FeeService feeService
    ) {
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.departmentRepo = departmentRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.feeService = feeService;
    }

    // Paginator
    public Page<CourseResponseDto> getAllCourses(Pageable pageable) {
        return courseRepo.findAll(pageable).map(this::convertToDto);
    }

    public List<CourseResponseDto> getAllCourses() {
        return courseRepo.findAll().stream().map(this::convertToDto).toList();
    }

    public List<CourseResponseDto> getCoursesByDepartment(Long departmentId) {
        return courseRepo.findByDepartment_Id(departmentId)
                .stream().map(this::convertToDto).toList();
    }

    public CourseResponseDto getCourseById(int id) {
        Course course = courseRepo.findById((long) id).orElse(null);
        if (course == null) {
            return null;
        }
        return convertToDto(course);
    }

    public CourseResponseDto createCourse(CourseRequest request) {
        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Course newCourse = new Course();
        newCourse.setTitle(request.getTitle());
        newCourse.setDescription(request.getDescription());
        newCourse.setCourseCode(request.getCourseCode());
        newCourse.setCreditHours(request.getCreditHours());
        newCourse.setStatus(request.getStatus());
        newCourse.setDepartment(department);

        return convertToDto(courseRepo.save(newCourse));
    }

    /**
     * If credit hours change, fees of every semester that has this course
     * are recalculated.
     */
    @Transactional
    public CourseResponseDto updateCourse(int id, CourseRequest request) {
        Course course = courseRepo.findById((long) id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCourseCode(request.getCourseCode());
        course.setCreditHours(request.getCreditHours());
        course.setStatus(request.getStatus());
        course.setDepartment(department);

        Course updated = courseRepo.save(course);
        courseRepo.flush();

        enrollmentRepo.findAll().stream()
                .filter(e -> e.getCourse().getId().equals(updated.getId()))
                .filter(e -> e.getSemester() != null && !e.getSemester().isBlank())
                .map(e -> e.getStudent().getId() + "|" + e.getSemester().trim())
                .distinct()
                .forEach(key -> {
                    String[] parts = key.split("\\|", 2);
                    feeService.syncFeeForSemester(Long.valueOf(parts[0]), parts[1]);
                });

        return convertToDto(updated);
    }

    public void deleteCourse(int id) {
        if (enrollmentRepo.existsByCourse_Id((long) id)) {
            throw new IllegalArgumentException(
                    "Cannot delete a course that students are enrolled in. "
                            + "Remove the enrollments first or mark the course INACTIVE.");
        }

        courseRepo.deleteById((long) id);
    }

    @Transactional
    public void removeCourseFromStudent(int studentId, Long courseId) {
        if (!studentRepo.existsById((long) studentId)) {
            throw new RuntimeException("Student not found");
        }

        if (!courseRepo.existsById(courseId)) {
            throw new RuntimeException("Course not found");
        }

        Set<String> semesters = enrollmentRepo.findByStudent_Id((long) studentId)
                .stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .map(Enrollment::getSemester)
                .collect(Collectors.toSet());

        enrollmentRepo.deleteByStudent_IdAndCourse_Id((long) studentId, courseId);
        enrollmentRepo.flush();

        semesters.forEach(s -> feeService.syncFeeForSemester((long) studentId, s));
    }

    // calculate total courses
    public long getTotalCoursesCount() {
        return courseRepo.count();
    }

    private CourseResponseDto convertToDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCourseCode(),
                course.getCreditHours(),
                course.getStatus(),
                course.getDepartment().getId(),
                course.getDepartment().getName()
        );
    }
}