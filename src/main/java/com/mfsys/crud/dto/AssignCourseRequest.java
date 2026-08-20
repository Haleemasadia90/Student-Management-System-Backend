package com.mfsys.crud.dto;


import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import lombok.Setter;

@Getter
@Setter
public class AssignCourseRequest {

    @NotNull(message="Course ID IS required")
    private Long courseId;
}
