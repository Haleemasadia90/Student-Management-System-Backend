package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Entity.RefreshToken;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Entity.User;

import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.Repository.RefreshTokenRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.Repository.UserRepository;

import com.mfsys.crud.dto.*;

import com.mfsys.crud.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

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

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private EmailService emailService;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    /*
     * SecureRandom is used for generating OTPs.
     */
    private final SecureRandom secureRandom = new SecureRandom();


    // =========================================================
    // SIGNUP
    // =========================================================

    public UserResponseDto signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        Department department = departmentRepo
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Department not found"
                        )
                );

        User newUser = new User();

        newUser.setUsername(request.getUsername());

        newUser.setEmail(request.getEmail());

        newUser.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        newUser.setRole("STUDENT");

        newUser.setFullName(
                request.getFullName()
        );

        newUser.setPhone(
                request.getPhone()
        );

        newUser.setDepartment(
                department
        );

        newUser.setDateOfBirth(
                request.getDateOfBirth()
        );

        newUser.setGender(
                request.getGender()
        );

        newUser.setSemester(
                request.getSemester()
        );

        newUser.setAdmissionYear(
                request.getAdmissionYear()
        );

        User savedUser = userRepository.save(
                newUser
        );


        Student newStudent = new Student();

        newStudent.setUser(savedUser);

        studentRepo.save(newStudent);


        return new UserResponseDto(

                savedUser.getId(),

                savedUser.getUsername(),

                savedUser.getEmail(),

                savedUser.getRole(),

                savedUser.getFullName(),

                savedUser.getPhone(),

                savedUser.getDepartment().getName(),

                savedUser.getDateOfBirth(),

                savedUser.getGender(),

                savedUser.getSemester(),

                savedUser.getAdmissionYear()
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );


        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );


        if (!passwordMatches) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getRole()
                );


        String refreshTokenStr =
                jwtUtil.generateRefreshToken(
                        user.getEmail()
                );


        saveRefreshToken(
                refreshTokenStr,
                user.getEmail()
        );


        return new LoginResponse(

                accessToken,

                refreshTokenStr,

                user.getUsername(),

                user.getEmail(),

                user.getRole()
        );
    }


    // =========================================================
    // SAVE REFRESH TOKEN
    // =========================================================

    private void saveRefreshToken(
            String token,
            String email
    ) {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(token);

        refreshToken.setUserEmail(email);

        refreshToken.setExpiryDate(
                LocalDateTime.now()
                        .plusNanos(
                                refreshTokenExpirationMs
                                        * 1_000_000
                        )
        );

        refreshTokenRepo.save(
                refreshToken
        );
    }


    // =========================================================
    // REFRESH ACCESS TOKEN
    // =========================================================

    public RefreshTokenResponse refreshAccessToken(
            String requestRefreshToken
    ) {

        // Step 1: Database mein token dhoondo
        RefreshToken storedToken =
                refreshTokenRepo
                        .findByToken(requestRefreshToken)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid refresh token"
                                )
                        );


        // Step 2: Check revoked
        if (storedToken.isRevoked()) {

            throw new RuntimeException(
                    "Refresh token has been revoked"
            );
        }


        // Step 3: Check expiry
        if (storedToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshTokenRepo.delete(
                    storedToken
            );

            throw new RuntimeException(
                    "Refresh token has expired. Please login again."
            );
        }


        // Step 4: Validate JWT
        if (!jwtUtil.isTokenValid(
                requestRefreshToken
        )) {

            throw new RuntimeException(
                    "Invalid refresh token"
            );
        }


        // Step 5: Check token type
        if (!"refresh".equals(
                jwtUtil.extractTokenType(
                        requestRefreshToken
                )
        )) {

            throw new RuntimeException(
                    "Invalid token type"
            );
        }


        User user = userRepository
                .findByEmail(
                        storedToken.getUserEmail()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );


        // Generate new access token
        String newAccessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getRole()
                );


        // Rotate refresh token
        refreshTokenRepo.delete(
                storedToken
        );


        String newRefreshToken =
                jwtUtil.generateRefreshToken(
                        user.getEmail()
                );


        saveRefreshToken(
                newRefreshToken,
                user.getEmail()
        );


        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken
        );
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    public void logout(
            String refreshTokenStr
    ) {

        refreshTokenRepo
                .findByToken(refreshTokenStr)
                .ifPresent(
                        refreshTokenRepo::delete
                );
    }


    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );


        boolean oldPasswordMatches =
                passwordEncoder.matches(
                        request.getOldPassword(),
                        user.getPassword()
                );


        if (!oldPasswordMatches) {

            throw new RuntimeException(
                    "Old password is incorrect"
            );
        }


        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        userRepository.save(user);
    }


    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    public void forgotPassword(
            String email
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        /*
         * Generate a 6-digit OTP.
         *
         * Example:
         * 5271 -> 005271
         */
        String otp = String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );


        /*
         * IMPORTANT:
         *
         * Save HASHED OTP in database.
         * Never save the actual OTP.
         */
        user.setResetOtp(
                passwordEncoder.encode(otp)
        );


        /*
         * OTP will expire after 5 minutes.
         */
        user.setResetOtpExpiry(
                LocalDateTime.now()
                        .plusMinutes(5)
        );


        userRepository.save(user);


        /*
         * Send the ORIGINAL OTP to email.
         *
         * Email:
         * Your OTP is 123456
         */
        emailService.sendPasswordResetOtp(
                user.getEmail(),
                otp
        );
    }


    // =========================================================
    // VERIFY OTP
    // =========================================================

    public void verifyOtp(
            String email,
            String otp
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        /*
         * Check whether OTP exists.
         */
        if (user.getResetOtp() == null ||
                user.getResetOtpExpiry() == null) {

            throw new RuntimeException(
                    "No OTP request found. Please request a new OTP."
            );
        }


        /*
         * Check OTP expiry.
         */
        if (user.getResetOtpExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP."
            );
        }


        /*
         * IMPORTANT:
         *
         * Database contains HASHED OTP.
         *
         * Therefore we MUST use:
         *
         * passwordEncoder.matches()
         *
         * NOT equals().
         */
        if (!passwordEncoder.matches(
                otp,
                user.getResetOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP."
            );
        }

        /*
         * If we reach here:
         *
         * OTP is valid.
         */
    }


    // =========================================================
    // RESET PASSWORD
    // =========================================================

    public void resetPassword(
            ResetPasswordRequest request
    ) {

        User user = userRepository
                .findByEmail(
                        request.getEmail()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        /*
         * Check whether OTP exists.
         */
        if (user.getResetOtp() == null ||
                user.getResetOtpExpiry() == null) {

            throw new RuntimeException(
                    "No OTP has been requested."
            );
        }


        /*
         * Check expiry FIRST.
         */
        if (user.getResetOtpExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new one."
            );
        }


        /*
         * Verify OTP AGAIN.
         *
         * This is important for security.
         *
         * Even if someone directly calls:
         *
         * POST /reset-password
         *
         * without calling /verify-otp first,
         * the backend still checks the OTP.
         */
        if (!passwordEncoder.matches(
                request.getOtp(),
                user.getResetOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP."
            );
        }


        /*
         * Encode the new password.
         */
        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );


        /*
         * Delete OTP after successful reset.
         *
         * This makes the OTP single-use.
         */
        user.setResetOtp(null);

        user.setResetOtpExpiry(null);


        userRepository.save(user);
    }
}