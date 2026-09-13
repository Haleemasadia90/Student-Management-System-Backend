package com.mfsys.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class FeeSummaryDto {

    private double totalPaid;
    private double totalDue;
    private double totalOverall;
    private int paidPercentage;

}
