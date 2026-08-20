package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.CourseRequest;
import com.mfsys.crud.dto.CourseResponseDto;
import com.mfsys.crud.services.CourseService;
import jakarta.validation.Valid;
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
    public List<CourseResponseDto> getAllCourses() {
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
}
