package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.Dto.response.PayrollRespDto;
import com.springboot.EasyPay.enums.ComponentType;
import com.springboot.EasyPay.enums.PayrollStatus;
import com.springboot.EasyPay.mapper.PayrollMapper;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Payroll;
import com.springboot.EasyPay.model.PayrollComponent;
import com.springboot.EasyPay.repository.EmployeeRepository;
import com.springboot.EasyPay.repository.HRRepository;
import com.springboot.EasyPay.repository.PayrollComponentRepository;
import com.springboot.EasyPay.repository.PayrollRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final PayrollComponentRepository componentRepository;
    private final EmployeeRepository employeeRepository;
    private final HRRepository hrRepository;


    //@Transactional
    public PayrollRespDto createPayroll(Long employeeId,PayrollDto dto) {

        if (dto.basicSalary() == null
                || dto.basicSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Basic salary must be zero or greater");
        }

        if (dto.payPeriodStart() == null
                || dto.payPeriodEnd() == null
                || dto.payPeriodEnd().isBefore(dto.payPeriodStart())) {
            throw new IllegalArgumentException(
                    "Invalid pay period");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        boolean exists =
                payrollRepository
                        .existsByEmployee_IdAndPayPeriodStartAndPayPeriodEnd(
                                employeeId,
                                dto.payPeriodStart(),
                                dto.payPeriodEnd());

        if (exists) {
            throw new IllegalStateException(
                    "Payroll already exists for this pay period");
        }

        Payroll payroll = PayrollMapper.convertDtoToEntity(dto);
        payroll.setEmployee(employee);
        payroll.setTotalEarnings(BigDecimal.ZERO);
        payroll.setTotalDeductions(BigDecimal.ZERO);
        payroll.setNetSalary(BigDecimal.ZERO);
        payroll.setStatus(PayrollStatus.DRAFT);

        return PayrollMapper.convertEntityToDto(
                payrollRepository.save(payroll));
    }

    //@Transactional
    public PayrollRespDto processPayroll(
            Long payrollId,String hrUsername) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new RuntimeException("Payroll not found"));

        if (payroll.getStatus() != PayrollStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft payroll can be processed");
        }

        HR hr = hrRepository.getByUsername(hrUsername);

        List<PayrollComponent> components =
                componentRepository.findByPayroll_Id(payrollId);

        BigDecimal earnings = payroll.getBasicSalary();
        BigDecimal deductions = BigDecimal.ZERO;

        for (PayrollComponent component : components) {

            if (component.getComponentType()
                    == ComponentType.EARNING) {
                earnings = earnings.add(component.getAmount());
            } else {
                deductions = deductions.add(component.getAmount());
            }
        }

        BigDecimal netSalary = earnings.subtract(deductions);

        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException(
                    "Net salary cannot be negative");
        }

        payroll.setTotalEarnings(earnings);
        payroll.setTotalDeductions(deductions);
        payroll.setNetSalary(netSalary);
        payroll.setStatus(PayrollStatus.PROCESSED);
        payroll.setProcessedAt(LocalDateTime.now());
        payroll.setHr(hr);

        return PayrollMapper.convertEntityToDto(
                payrollRepository.save(payroll));
    }

    public PayrollRespDto markAsPaid(Long payrollId) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new RuntimeException("Payroll not found"));

        if (payroll.getStatus() != PayrollStatus.PROCESSED) {
            throw new IllegalStateException(
                    "Only processed payroll can be marked as paid");
        }

        payroll.setStatus(PayrollStatus.PAID);

        return PayrollMapper.convertEntityToDto(
                payrollRepository.save(payroll));
    }

    //@Transactional(readOnly = true)
    public PayrollRespDto getPayslip(Long payrollId) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new RuntimeException("Payslip not found"));

        return PayrollMapper.convertEntityToDto(payroll);
    }

    //@Transactional(readOnly = true)
    public List<PayrollRespDto> getEmployeePayslips(
            Long employeeId,
            org.springframework.data.domain.Pageable pageable) {

        return payrollRepository.getPayrollByEmployeeId(
                employeeId, pageable);
    }
}
