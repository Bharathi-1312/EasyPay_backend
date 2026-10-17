package com.springboot.EasyPay.Dto.request;

import com.springboot.EasyPay.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record PayrollDto(
        @NotNull(message = "Pay period start date is required")
        LocalDate payPeriodStart,

        @NotNull(message = "Pay period end date is required")
        LocalDate payPeriodEnd,

        @Positive(message = "Basic salary must be greater than zero")
        double basicSalary,

        @PositiveOrZero(message = "Total earnings cannot be negative")
        double totalEarnings,

        @PositiveOrZero(message = "Total deductions cannot be negative")
        double totalDeductions,

        @PositiveOrZero(message = "Net salary cannot be negative")
        double netSalary,

        @NotNull(message="Status should be filled")
        Status status
) {
}
