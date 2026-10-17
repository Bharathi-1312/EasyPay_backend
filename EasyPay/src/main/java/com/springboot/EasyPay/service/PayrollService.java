package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.enums.Status;
import com.springboot.EasyPay.exception.ResourceNotFoundException;
import com.springboot.EasyPay.mapper.PayrollMapper;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Payroll;
import com.springboot.EasyPay.repository.EmployeeRepository;
import com.springboot.EasyPay.repository.HRRepository;
import com.springboot.EasyPay.repository.PayrollRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final HRRepository hrRepository;
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public void addPayrollByHr(Long hrId,Long employeeId, @Valid PayrollDto payrollDto) {
        HR hr = hrRepository.findById(hrId)
                .orElseThrow(()-> new ResourceNotFoundException("HR  not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));

        Payroll payroll = PayrollMapper.mapDtoToEntity(payrollDto);
        payroll.setStatus(Status.PENDING);
        payroll.setEmployee(employee);
        payroll.setHr(hr);
        payrollRepository.save(payroll);
    }


}
