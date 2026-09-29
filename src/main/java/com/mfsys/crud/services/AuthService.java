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
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

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

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());
        String refreshTokenStr = jwtUtil.generateRefreshToken(user.getEmail());

        saveRefreshToken(refreshTokenStr, user.getEmail());

        return new LoginResponse(
                accessToken,
                refreshTokenStr,
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    private void saveRefreshToken(String token, String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserEmail(email);
        refreshToken.setExpiryDate(java.time.LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000));
        refreshTokenRepo.save(refreshToken);
    }

    public RefreshTokenResponse refreshAccessToken(String requestRefreshToken) {

        // Step 1: Database mein token dhoondo
        RefreshToken storedToken = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Step 2: Check karo revoke to nahi hua
        if (storedToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        // Step 3: Check karo expire to nahi hua (database record se)
        if (storedToken.getExpiryDate().isBefore(java.time.LocalDateTime.now())) {
            refreshTokenRepo.delete(storedToken);
            throw new RuntimeException("Refresh token has expired. Please login again.");
        }

        // Step 4: JWT signature bhi valid honi chahiye
        if (!jwtUtil.isTokenValid(requestRefreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Step 5: Confirm karo ye actually ek "refresh" type token hai, "access" nahi
        if (!"refresh".equals(jwtUtil.extractTokenType(requestRefreshToken))) {
            throw new RuntimeException("Invalid token type");
        }

        User user = userRepository.findByEmail(storedToken.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Naya access token banao
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole());

        // Refresh token bhi ROTATE karte hain (security best-practice — purana delete, naya banao)
        refreshTokenRepo.delete(storedToken);
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        saveRefreshToken(newRefreshToken, user.getEmail());

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshTokenStr) {
        refreshTokenRepo.findByToken(refreshTokenStr).ifPresent(refreshTokenRepo::delete);
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

        String otp = String.format("%06d", java.util.concurrent.ThreadLocalRandom
                        .current()
                        .nextInt(1_000_000)
        );
        user.setResetOtp(otp);
        user.setResetOtpExpiry(
                java.time.LocalDateTime.now().plusMinutes(5)
        );
        userRepository.save(user);
        emailService.sendPasswordResetOtp(
                user.getEmail(),
                otp
        );
    }

    public void verifyOtp(
            String email,
            String otp
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );

        if (user.getResetOtp() == null) {
            throw new RuntimeException(
                    "No OTP has been requested."
            );
        }

        if (!user.getResetOtp().equals(otp)) {
            throw new RuntimeException(
                    "Invalid OTP."
            );
        }

        if (user.getResetOtpExpiry() == null ||
                user.getResetOtpExpiry()
                        .isBefore(java.time.LocalDateTime.now())) {

            throw new RuntimeException(
                    "OTP has expired. Please request a new one."
            );
        }
    }

    public void resetPassword(
            ResetPasswordRequest request
    ) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new RuntimeException(
                        "No account found with this email"
                )
        );

        if (user.getResetOtp() == null) {
            throw new RuntimeException("No OTP has been requested.");
        }

        if (!user.getResetOtp().equals(request.getOtp())) {
            throw new RuntimeException(
                    "Invalid OTP."
            );
        }

        if (user.getResetOtpExpiry() == null || user.getResetOtpExpiry() .isBefore(java.time.LocalDateTime.now())) {

            throw new RuntimeException( "OTP has expired. Please request a new one." );
        }

        user.setPassword( passwordEncoder.encode(request.getNewPassword())
        );

        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        userRepository.save(user);
    }

}