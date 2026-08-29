package com.mfsys.crud.services;

import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Entity.Student;
import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.Repository.StudentRepo;
import com.mfsys.crud.Repository.UserRepository;
import com.mfsys.crud.dto.LoginRequest;
import com.mfsys.crud.dto.LoginResponse;
import com.mfsys.crud.dto.SignupRequest;
import com.mfsys.crud.dto.UserResponseDto;
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


}