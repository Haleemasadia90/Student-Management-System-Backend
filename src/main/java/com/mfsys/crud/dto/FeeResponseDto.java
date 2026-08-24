package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FeeResponseDto {
    private Long id;
    private String studentUsername;
    private String semester;
    private double totalFee;
    private double paidFee;
    private double dueAmount;
    private String status;
}