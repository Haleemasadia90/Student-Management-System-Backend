package com.mfsys.crud.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter


@Entity

public class Course {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable=true)
    private String title;

    @Column(nullable = true)
    private  String description;

@ManyToMany(mappedBy = "courses")
private List<Student> students;

}
