package com.springboot.EasyPay.controller;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.Dto.response.PayrollComponentRespDto;
import com.springboot.EasyPay.service.PayrollComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

public class PayrollComponentController {

    private final PayrollComponentService componentService;

    @PostMapping("/api/payroll-components/{payrollId}")
//    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("hasRole('HR')")
    public PayrollComponentRespDto addComponent(@PathVariable Long payrollId,
            @RequestBody PayrollComponentDto dto) {
        return componentService.addComponent(payrollId,dto);
    }

    @GetMapping("/api/payroll-components/payroll/{payrollId}")
   // @PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    public List<PayrollComponentRespDto> getComponents(
            @PathVariable Long payrollId) {
        return componentService.getComponents(payrollId);
    }

    @DeleteMapping("/api/payroll-components/{componentId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasRole('HR')")
    public void deleteComponent(
            @PathVariable Long componentId) {
        componentService.deleteComponent(componentId);
    }
}
