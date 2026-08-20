package com.mfsys.crud;

import com.mfsys.crud.Entity.User;
import com.mfsys.crud.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;


@SpringBootApplication
public class CrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByEmail("admin@registrar.edu").isEmpty()) {

				User admin = new User();
				admin.setUsername("Admin");
				admin.setEmail("admin@registrar.edu");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole("ADMIN");

				userRepository.save(admin);
				System.out.println(" Admin account created: admin@registrar.edu");
			} else {
				System.out.println(" Admin already exists, skipping seed.");
			}
		};
	}
}