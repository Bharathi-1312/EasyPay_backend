package com.springboot.EasyPay.Dto.request;

import com.springboot.EasyPay.enums.ComponentName;
import com.springboot.EasyPay.enums.ComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PayrollComponentDto(@NotNull(message = "Component name is required")
                                  ComponentName componentName,

                                  @NotNull(message = "Component type is required")
                                  ComponentType componentType,

                                  @PositiveOrZero(message = "Amount cannot be negative")
                                   double amount,

                                  @NotBlank(message = "Description cannot be blank")
                                   String description) {
}
