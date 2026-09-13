package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudentResponseDto {
    private Long id;
    private String fullName;
    private String phone;
    private String username;
    private String email;
    private String department;
    private Long departmentId;
    private List<String> courseTitles;
    private int totalCreditHours;
    private int maxCreditHours;
    private String profilePicture;
    private Integer admissionYear;
    private String currentSemester;
    private int currentSemesterNumber;
    private int totalSemesters;
}