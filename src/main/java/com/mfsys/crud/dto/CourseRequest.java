package com.mfsys.crud.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private  String description;

    @NotBlank(message = "Course code is required")
    private String courseCode;

    @Min(value = 1, message = "Credit hours must be at least 1")
    private int creditHours;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Department is required")
    private Long departmentId;
}
