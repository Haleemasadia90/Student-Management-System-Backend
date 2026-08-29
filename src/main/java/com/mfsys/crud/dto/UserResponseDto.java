package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private String department;
    private String dateOfBirth;
    private String gender;
    private String semester;
    private int admissionYear;
}