package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeeResponseDto {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private String semester;
    private double ratePerCreditHour;
    private double fixedSemesterCharge;
    private double totalFee;
    private double paidFee;
    private double dueAmount;
    private String status;
}