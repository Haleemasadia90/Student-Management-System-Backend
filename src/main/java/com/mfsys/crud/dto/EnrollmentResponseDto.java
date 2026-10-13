package com.mfsys.crud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnrollmentResponseDto {

    private Long id;

    private Long studentId;

    private Long courseId;

    private String courseTitle;

    private String courseCode;

    private int creditHours;

    private String courseStatus;

    private String semester;
}