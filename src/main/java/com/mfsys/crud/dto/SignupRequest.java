package com.mfsys.crud.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupRequest {

    @NotBlank(message = "Username is required")
    String username;

    @NotBlank(message = "emai is requires")
    @Email(message = "Enter a valid email")
    String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
