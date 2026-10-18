package com.mfsys.crud.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "fee",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fee_student_semester",
                        columnNames = {"student_id", "semester"}
                )
        }
)
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private double ratePerCreditHour;

    @Column(nullable = false)
    private double fixedSemesterCharge;

    @Column(nullable = false)
    private double totalFee;

    @Column(nullable = false)
    private double paidFee;

    @Column(nullable = false)
    private String status;
}