package com.mfsys.crud.Controller;

import com.mfsys.crud.dto.LoginRequest;
import com.mfsys.crud.dto.LoginResponse;
import com.mfsys.crud.dto.SignupRequest;
import com.mfsys.crud.dto.UserResponseDto;
import com.mfsys.crud.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ============ SIGNUP (already bana hua) ============
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            UserResponseDto newUser = authService.signup(request);
            return ResponseEntity.ok(newUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============ LOGIN (naya) ============
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }


    // AuthController.java me temporarily add karein testing ke liye
    @GetMapping("/test-admin")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdminOnly() {
        return ResponseEntity.ok("Admin login!");
    }
}