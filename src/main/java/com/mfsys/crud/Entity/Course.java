//package com.mfsys.crud.Entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.List;
//
//@Getter
//@Setter
//
//
//@Entity
//
//public class Course {
//
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    @Column(nullable=true)
//    private String title;
//
//    @Column(nullable = true)
//    private  String description;
//
//    @Column(nullable = false, unique = true)
//    private String courseCode;
//
//    @Column(nullable = false)
//    private int creditHours;
//
//
//    @Column(nullable = false)
//    private String status;
//
//    @ManyToOne
//    @JoinColumn(name = "department_id", nullable = false)
//    private Department department;
//
//
//    @ManyToMany(mappedBy = "courses")
//private List<Student> students;
//
//}

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

    @Column(nullable = true)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false, unique = true)
    private String courseCode;

    @Column(nullable = false)
    private int creditHours;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;
}