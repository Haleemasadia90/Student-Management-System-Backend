//package com.mfsys.crud.Entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//@Entity
//public class Student {
//    @Id
//
//    private int studentID;
//
//    @Column(nullable = false)
//    private String fname;
//
//    @Column(nullable = false, unique = true)
//    private String email;
//}



//
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
//@Entity
//public class Student {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
//    private User user;
//
//
//    @ManyToMany
//    @JoinTable(name = "student_courses" , joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns =@JoinColumn(name="course_id"))
//    private List<Course> courses;
//}





package com.mfsys.crud.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true
    )
    private User user;

    @OneToMany(
            mappedBy = "student",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Enrollment> enrollments;
}