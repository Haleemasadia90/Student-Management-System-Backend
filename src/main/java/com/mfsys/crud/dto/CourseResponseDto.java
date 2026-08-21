package com.mfsys.crud.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseResponseDto {

    private Long id;
    private String title;
    private String description;
    private String courseCode;
    private int creditHours;
    private String status;
}
