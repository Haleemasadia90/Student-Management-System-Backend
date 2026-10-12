//package com.mfsys.crud;
//
//import com.mfsys.crud.Entity.User;
//import com.mfsys.crud.Repository.UserRepository;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//
//@SpringBootApplication
//public class CrudApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(CrudApplication.class, args);
//	}
//
//	@Bean
//	CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//		return args -> {
//			if (userRepository.findByEmail("admin@registrar.edu").isEmpty()) {
//
//				User admin = new User();
//				admin.setUsername("Admin");
//				admin.setEmail("admin@registrar.edu");
//				admin.setPassword(passwordEncoder.encode("admin123"));
//				admin.setRole("ADMIN");
//				admin.setFullName("System Admin");
//				admin.setPhone("0000000000");
//
//
//				admin.setDateOfBirth("2000-01-01");
//				admin.setGender("Other");
//				admin.setSemester("N/A");
//				admin.setAdmissionYear(2026);
//
//				userRepository.save(admin);
//				System.out.println(" Admin account created: admin@registrar.edu");
//			} else {
//				System.out.println(" Admin already exists, skipping seed.");
//			}
//		};
//	}
//}




package com.mfsys.crud;

import com.mfsys.crud.Entity.Department;
import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.DepartmentRepo;
import com.mfsys.crud.Repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(
			UserRepository userRepository,
			DepartmentRepo departmentRepo,
			PasswordEncoder passwordEncoder) {

		return args -> {

			// Create default department if it doesn't exist
//			Department department;
//
//			if (departmentRepo.findAll().isEmpty()) {
//				department = new Department();
//				department.setName("");
//				department = departmentRepo.save(department);
//			} else {
//				department = departmentRepo.findAll().get(0);
//			}

			// Create admin
			if (userRepository.findByEmail("admin@registrar.edu").isEmpty()) {

				User admin = new User();

				admin.setUsername("Admin");
				admin.setEmail("admin@registrar.edu");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole("ADMIN");
				admin.setFullName("System Admin");
				admin.setPhone("0000000000");

				// IMPORTANT
//				admin.setDepartment(department);


				admin.setDateOfBirth("2000-01-01");
				admin.setGender("Other");
				admin.setSemester("N/A");
				admin.setAdmissionYear(2026);

				userRepository.save(admin);

				System.out.println("Admin account created: admin@registrar.edu");

			} else {
				System.out.println("Admin already exists, skipping seed.");
			}
		};
	}
}