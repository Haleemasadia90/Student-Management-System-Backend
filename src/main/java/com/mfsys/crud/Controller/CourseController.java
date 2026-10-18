package com.mfsys.crud.Controller;

import com.mfsys.crud.common.ApiResponse;
import com.mfsys.crud.dto.CourseRequest;
import com.mfsys.crud.dto.CourseResponseDto;
import com.mfsys.crud.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService){
        this.courseService = courseService;
    }



    @GetMapping
    public List<CourseResponseDto> getAllCourses(@RequestParam(required = true) Long departmentId) {
        if (departmentId != null) {
            return courseService.getCoursesByDepartment(departmentId);
        }
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseResponseDto getCourseById(@PathVariable int id){
        return courseService.getCourseById(id);

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponseDto createCourse(@Valid @RequestBody CourseRequest request)
    {
        return courseService.createCourse(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CourseResponseDto updateCourse(@PathVariable int id, @Valid @RequestBody CourseRequest request){
        return courseService.updateCourse(id,request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourse(@PathVariable int id){
        courseService.deleteCourse((id));
    }


//    calculate total courses
@GetMapping("/count")
@PreAuthorize("hasRole('ADMIN')")
public long getTotalCourses() {
    return courseService.getTotalCoursesCount();
}


    // paginated active courses of a department (student "Available Courses")
    @GetMapping("/available/paged")
    public ResponseEntity<ApiResponse<Page<CourseResponseDto>>> getAvailableCourses(
            @RequestParam Long departmentId,
            @PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC)
            Pageable pageable) {

        Page<CourseResponseDto> page =
                courseService.getAvailableCoursesPaged(departmentId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("SUCC_CRS_002",
                        "Available courses fetched successfully", page));
    }

//paginator
@GetMapping("/paged")
public ResponseEntity<ApiResponse<Page<CourseResponseDto>>> getCourses(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
        Pageable pageable) {

    Page<CourseResponseDto> page = courseService.getAllCourses(pageable);

    return ResponseEntity.ok(
            ApiResponse.success("SUCC_CRS_001", "Courses fetched successfully", page));
}

}
