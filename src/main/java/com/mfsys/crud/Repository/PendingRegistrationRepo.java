package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingRegistrationRepo
        extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void deleteByEmail(String email);
}