package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.*;
import com.mfsys.crud.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;



    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest request
    ) {

        try {

            UserResponseDto newUser =
                    authService.signup(request);

            return ResponseEntity.ok(newUser);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        try {

            LoginResponse response =
                    authService.login(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(401)
                    .body(e.getMessage());
        }
    }



    @GetMapping("/test-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdminOnly() {

        return ResponseEntity.ok(
                "Admin login!"
        );
    }




    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        try {

            String email =
                    authentication.getName();

            authService.changePassword(
                    email,
                    request
            );

            return ResponseEntity.ok(
                    "Password changed successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }




    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {

        try {

            authService.forgotPassword(
                    request.getEmail()
            );

            return ResponseEntity.ok(
                    "OTP sent to your email"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================================================
// RESEND REGISTRATION OTP
// =========================================================

    @PostMapping("/resend-registration-otp")
    public ResponseEntity<?> resendRegistrationOtp(
            @RequestBody VerifyOtpRequest request
    ) {

        try {

            authService.resendRegistrationOtp(
                    request.getEmail()
            );

            return ResponseEntity.ok(
                    "Registration OTP resent successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


// =========================================================
// RESEND FORGOT PASSWORD OTP
// =========================================================

    @PostMapping("/resend-forgot-password-otp")
    public ResponseEntity<?> resendForgotPasswordOtp(
            @RequestBody ForgotPasswordRequest request
    ) {

        try {

            authService.resendForgotPasswordOtp(
                    request.getEmail()
            );

            return ResponseEntity.ok(
                    "OTP resent successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request
    ) {

        try {

            authService.verifyOtp(
                    request.getEmail(),
                    request.getOtp()
            );

            return ResponseEntity.ok(
                    "OTP verified successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/verify-registration-otp")
    public ResponseEntity<?> verifyRegistrationOtp(
            @RequestBody VerifyOtpRequest request
    ) {

        authService.verifyRegistrationOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                "Registration completed successfully"
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        try {

            authService.resetPassword(
                    request
            );

            return ResponseEntity.ok(
                    "Password reset successfully"
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }




    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        try {

            RefreshTokenResponse response =
                    authService.refreshAccessToken(
                            request.getRefreshToken()
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(401)
                    .body(e.getMessage());
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        authService.logout(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                "Logged out successfully"
        );
    }
}