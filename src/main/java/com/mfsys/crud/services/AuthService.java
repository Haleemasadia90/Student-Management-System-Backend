package com.mfsys.crud.services;

import com.mfsys.crud.Entity.*;
import com.mfsys.crud.Repository.*;
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
    private PendingRegistrationRepo pendingRegistrationRepo;

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

    private final SecureRandom secureRandom = new SecureRandom();

    private String generateOtp() {
        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
    }

    private LocalDateTime getOtpExpiry() {
        return LocalDateTime.now().plusMinutes(2);
    }

    public UserResponseDto signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        Department department = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        PendingRegistration pendingRegistration = pendingRegistrationRepo.findByEmail(request.getEmail()).orElse(new PendingRegistration());

        String otp = generateOtp();

        pendingRegistration.setUsername(request.getUsername());
        pendingRegistration.setEmail(request.getEmail());

        pendingRegistration.setPassword(passwordEncoder.encode(request.getPassword()) );

        pendingRegistration.setFullName(request.getFullName());
        pendingRegistration.setPhone(request.getPhone());
        pendingRegistration.setDepartmentId(department.getId());
        pendingRegistration.setDateOfBirth(request.getDateOfBirth());
        pendingRegistration.setGender(request.getGender());
        pendingRegistration.setSemester(request.getSemester());
        pendingRegistration.setAdmissionYear(request.getAdmissionYear());

        pendingRegistration.setOtp(passwordEncoder.encode(otp));

        pendingRegistration.setOtpExpiry(getOtpExpiry());


        pendingRegistrationRepo.save(pendingRegistration);

        emailService.sendPasswordResetOtp( request.getEmail(), otp);

        return new UserResponseDto(
                null,
                request.getUsername(),
                request.getEmail(),
                "STUDENT",
                request.getFullName(),
                request.getPhone(),
                department.getName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getSemester(),
                request.getAdmissionYear()
        );
    }


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


        if (storedToken.isRevoked()) {

            throw new RuntimeException(
                    "Refresh token has been revoked"
            );
        }

        if (storedToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            refreshTokenRepo.delete(
                    storedToken
            );

            throw new RuntimeException(
                    "Refresh token has expired. Please login again."
            );
        }

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


        String newAccessToken =
                jwtUtil.generateAccessToken(
                        user.getEmail(),
                        user.getRole()
                );


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


    public void logout(
            String refreshTokenStr
    ) {

        refreshTokenRepo
                .findByToken(refreshTokenStr)
                .ifPresent(
                        refreshTokenRepo::delete
                );
    }

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

    public void forgotPassword(
            String email
    ) {

        User user = userRepository .findByEmail(email).orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        String otp = generateOtp();

        user.setResetOtp(passwordEncoder.encode(otp));

        user.setResetOtpExpiry(getOtpExpiry()
        );


        userRepository.save(user);

        emailService.sendPasswordResetOtp(user.getEmail(), otp
        );
    }

    public void resendRegistrationOtp(String email) {

        PendingRegistration pendingRegistration = pendingRegistrationRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("No pending registration found with this email"));


        String otp = generateOtp();

        pendingRegistration.setOtp(passwordEncoder.encode(otp));

        pendingRegistration.setOtpExpiry(getOtpExpiry());

        pendingRegistrationRepo.save(pendingRegistration);

        emailService.sendPasswordResetOtp(email, otp);
    }


    public void resendForgotPasswordOtp(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );

        String otp = generateOtp();

        user.setResetOtp(
                passwordEncoder.encode(otp)
        );

        user.setResetOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        userRepository.save(user);

        emailService.sendPasswordResetOtp(
                email,
                otp
        );
    }


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

        if (user.getResetOtp() == null ||
                user.getResetOtpExpiry() == null) {

            throw new RuntimeException(
                    "No OTP request found. Please request a new OTP."
            );
        }

        if (user.getResetOtpExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP."
            );
        }

        if (!passwordEncoder.matches(
                otp,
                user.getResetOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP."
            );
        }
    }


    public void verifyRegistrationOtp(
            String email,
            String otp
    ) {

        PendingRegistration pendingRegistration =
                pendingRegistrationRepo
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No pending registration found with this email"
                                )
                        );

        if (pendingRegistration.getOtp() == null ||
                pendingRegistration.getOtpExpiry() == null) {

            throw new RuntimeException(
                    "No OTP found. Please register again."
            );
        }

        if (pendingRegistration.getOtpExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please register again."
            );
        }

        // 4. Verify hashed OTP
        if (!passwordEncoder.matches(
                otp,
                pendingRegistration.getOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP."
            );
        }

        if (userRepository.existsByEmail(
                pendingRegistration.getEmail()
        )) {

            throw new RuntimeException(
                    "Email is already registered."
            );
        }

        if (userRepository.existsByUsername(
                pendingRegistration.getUsername()
        )) {

            throw new RuntimeException(
                    "Username is already taken."
            );
        }

        Department department =
                departmentRepo.findById(
                                pendingRegistration.getDepartmentId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Department not found"
                                )
                        );

        User newUser = new User();

        newUser.setUsername(
                pendingRegistration.getUsername()
        );

        newUser.setEmail(
                pendingRegistration.getEmail()
        );
        newUser.setPassword(
                pendingRegistration.getPassword()
        );

        newUser.setRole("STUDENT");

        newUser.setFullName(
                pendingRegistration.getFullName()
        );

        newUser.setPhone(
                pendingRegistration.getPhone()
        );

        newUser.setDepartment(
                department
        );

        newUser.setDateOfBirth(
                pendingRegistration.getDateOfBirth()
        );

        newUser.setGender(
                pendingRegistration.getGender()
        );

        newUser.setSemester(
                pendingRegistration.getSemester()
        );

        newUser.setAdmissionYear(
                pendingRegistration.getAdmissionYear()
        );

        User savedUser = userRepository.save(
                newUser
        );

        Student newStudent = new Student();

        newStudent.setUser(savedUser);

        studentRepo.save(newStudent);

        pendingRegistrationRepo.delete(
                pendingRegistration
        );
    }

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

        if (user.getResetOtp() == null ||
                user.getResetOtpExpiry() == null) {

            throw new RuntimeException(
                    "No OTP has been requested."
            );
        }

        if (user.getResetOtpExpiry()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new one."
            );
        }

        if (!passwordEncoder.matches(
                request.getOtp(),
                user.getResetOtp()
        )) {

            throw new RuntimeException(
                    "Invalid OTP."
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        user.setResetOtp(null);

        user.setResetOtpExpiry(null);


        userRepository.save(user);
    }
}