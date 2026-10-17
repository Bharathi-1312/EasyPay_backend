package com.springboot.EasyPay.controller;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.service.PayrollComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PayrollComponentController {
    private final PayrollComponentService payrollComponentService;

    @PostMapping("api/payrollcomponent/add/{payrollId}")
    public void add(@PathVariable  Long payrollId,
            @Valid @RequestBody PayrollComponentDto payrollComponentDto)
    {
        payrollComponentService.add(payrollId,payrollComponentDto);
    }
}
