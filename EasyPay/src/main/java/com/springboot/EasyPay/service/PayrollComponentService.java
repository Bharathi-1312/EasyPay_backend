package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.exception.ResourceNotFoundException;
import com.springboot.EasyPay.mapper.PayrollComponentMapper;
import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Payroll;
import com.springboot.EasyPay.model.PayrollComponents;
import com.springboot.EasyPay.repository.PayrollComponentRepository;
import com.springboot.EasyPay.repository.PayrollRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollComponentService {
    private final PayrollComponentRepository payrollComponentRepository;
    private final PayrollRepository payrollRepository;
    public void add(Long payrollId,@Valid PayrollComponentDto payrollComponentDto) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(()-> new ResourceNotFoundException("PAyrollId  not found"));

        PayrollComponents payrollComponents= PayrollComponentMapper.mapDtoToEntity(payrollComponentDto);

        payrollComponents.setPayroll(payroll);
        payrollComponentRepository.save(payrollComponents);

    }
}
