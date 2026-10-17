package com.springboot.EasyPay.controller;

import com.springboot.EasyPay.Dto.request.EmployeeDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.Dto.response.PayrollRespDto;
import com.springboot.EasyPay.service.PayrollService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
//@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/api/payroll/{employeeId}")
    //@ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasRole('HR')")
    public PayrollRespDto createPayroll(@PathVariable Long employeeId
                                            ,@RequestBody PayrollDto dto) {
        return payrollService.createPayroll(employeeId,dto);
    }

    @PostMapping("/api/payroll/process/{payrollId}")
   // @PreAuthorize("hasRole('HR')")
    public PayrollRespDto processPayroll(
            @PathVariable Long payrollId,
            Principal principal) {
        return payrollService.processPayroll(payrollId,principal.getName());
    }

    @PutMapping("/api/payroll/pay/{payrollId}")
    //@PreAuthorize("hasRole('HR')")
    public PayrollRespDto markAsPaid(@PathVariable Long payrollId) {
        return payrollService.markAsPaid(payrollId);
    }

    @GetMapping("/api/payroll/payslip/{payrollId}")
    //@PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    public PayrollRespDto getPayslip(@PathVariable Long payrollId) {
        return payrollService.getPayslip(payrollId);
    }

    @GetMapping("/api/payroll/employee/{employeeId}")
    //@PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    public List<PayrollRespDto> getEmployeePayslips(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid page or size");
        }

        Pageable pageable = PageRequest.of(page, size);

        return payrollService.getEmployeePayslips(
                employeeId, pageable);
    }
}
