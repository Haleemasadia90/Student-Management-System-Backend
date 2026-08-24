package com.mfsys.crud.Repository;

import com.mfsys.crud.Entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepo extends JpaRepository<Fee, Long> {

    List<Fee> findByStudent_Id(Long studentId);

    List<Fee> findByStudent_User_Email(String email);
}