package com.springboot.EasyPay.controller;

import com.springboot.EasyPay.Dto.request.EmployeeDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("api/payroll/add/{hrId}/{employeeId}")
    public void addPayrollByHr(@PathVariable  Long hrId,
                                @PathVariable Long employeeId,
                                @Valid @RequestBody PayrollDto payrollDto){
        payrollService.addPayrollByHr(hrId,employeeId, payrollDto);
    }
}
