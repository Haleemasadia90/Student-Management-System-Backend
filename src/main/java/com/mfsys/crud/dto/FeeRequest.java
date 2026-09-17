package com.mfsys.crud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeRequest {

    @NotBlank(message = "Semester is required")
    private String semester;

    @Min(value = 0, message = "Total fee cannot be negative")
    private double totalFee;

    @Min(value = 0, message = "Paid fee cannot be negative")
    private double paidFee;

}