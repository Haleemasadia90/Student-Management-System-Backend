package com.mfsys.crud.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Student {
    @Id
    private int studentID;

    @Column(nullable = false)
    private String fname;

    @Column(nullable = false)
    private String email;
}
