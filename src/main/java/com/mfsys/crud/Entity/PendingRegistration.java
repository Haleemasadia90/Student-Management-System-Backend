package com.mfsys.crud.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "pending_registrations")
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private int admissionYear;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime otpExpiry;
}