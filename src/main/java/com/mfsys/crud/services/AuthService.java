package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.Repository.UserRepository;
import com.mfsys.crud.dto.*;
import com.mfsys.crud.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepo studentRepo;


    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserResponseDto signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("STUDENT");
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setDepartment(department);   // ab Entity-object set ho raha hai
        newUser.setDateOfBirth(request.getDateOfBirth());
        newUser.setGender(request.getGender());
        newUser.setSemester(request.getSemester());
        newUser.setAdmissionYear(request.getAdmissionYear());

        User savedUser = userRepository.save(newUser);

        Student newStudent = new Student();
        newStudent.setUser(savedUser);
        studentRepo.save(newStudent);

        return new UserResponseDto(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole(),
                savedUser.getFullName(), savedUser.getPhone(),
                savedUser.getDepartment().getName(),   // naam nikal rahe hain
                savedUser.getDateOfBirth(), savedUser.getGender(), savedUser.getSemester(), savedUser.getAdmissionYear()
        );
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }


    public void changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean oldPasswordMatches = passwordEncoder.matches(request.getOldPassword(), user.getPassword());

        if (!oldPasswordMatches) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }


    @Autowired
    private EmailService emailService;

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String resetLink = "http://localhost:4200/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset link"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Reset link has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

}