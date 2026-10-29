package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.Dto.response.*;
import com.springboot.EasyPay.enums.ComponentName;
import com.springboot.EasyPay.enums.ComponentType;
import com.springboot.EasyPay.enums.PayrollStatus;
import com.springboot.EasyPay.mapper.PayrollComponentMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
                componentRepository.findByPayrollId(payrollId);

        BigDecimal earnings = payroll.getBasicSalary();
        BigDecimal deductions = BigDecimal.ZERO;
        BigDecimal hundred=BigDecimal.valueOf(100);

        for (PayrollComponent component : components) {
            BigDecimal Percent=component.getAmountPercentage().divide(hundred, 4, RoundingMode.HALF_UP);
            BigDecimal amt=Percent.multiply(payroll.getBasicSalary());

            if (component.getComponentType()
                    == ComponentType.EARNING) {
                earnings = earnings.add(amt);
            } else {
                deductions = deductions.add(amt);
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
            Pageable pageable) {

        return payrollRepository.getPayrollByEmployeeId(
                employeeId, pageable);
    }





    // API 14: Payroll processed by a particular HR
    //@Transactional(readOnly = true)
    public List<Payroll> getProcessedByHr(Long hrId) {
        return payrollRepository.findPayrollsProcessedByHr(hrId);
    }
    // API 2: Filter by status
    //@Transactional(readOnly = true)
    public List<PayrollRespDto> getByStatus(PayrollStatus status) {
        List<Payroll> payrolls = payrollRepository.findByStatus(status);

        List<PayrollRespDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.convertEntityToDto(payroll));
        }

        return result;
    }

    // API 3: Payrolls within a date range
    //@Transactional(readOnly = true)
    public List<PayrollRespDto> getByPeriod(
            LocalDate start, LocalDate end) {

        validateDates(start, end);

        List<Payroll> payrolls =
                payrollRepository.findByPeriod(start, end);

        List<PayrollRespDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.convertEntityToDto(payroll));
        }

        return result;
    }

    // API 4: Search by employee and date range
    //@Transactional(readOnly = true)
    public List<PayrollRespDto> searchPayroll(
            Long employeeId, LocalDate start, LocalDate end) {

        validateDates(start, end);

        List<Payroll> payrolls =
                payrollRepository.searchPayroll(employeeId, start, end);

        List<PayrollRespDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.convertEntityToDto(payroll));
        }

        return result;
    }


    // API 7: Earnings history
    //@Transactional(readOnly = true)
    public List<PayrollEarningsDto> getEarnings(Long employeeId) {

        List<Payroll> payrolls =
                payrollRepository.findEmployeePayrollHistory(employeeId);

        List<PayrollEarningsDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.toEarningsResponse(payroll));
        }

        return result;
    }

    // API 8: Deduction history
    //@Transactional(readOnly = true)
    public List<PayrollDeductionDto> getDeductions(Long employeeId) {

        List<Payroll> payrolls =
                payrollRepository.findEmployeePayrollHistory(employeeId);

        List<PayrollDeductionDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.toDeductionsResponse(payroll));
        }

        return result;
    }


    //@Transactional(readOnly = true)
    public MonthlyPaymentDto monthlyReport(
            int year, int month) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month must be between 1 and 12");
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Payroll> payrolls =
                payrollRepository.findPayrollsOverlappingPeriod(start, end);

        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal totalDeductions = BigDecimal.ZERO;
        BigDecimal totalNetSalary = BigDecimal.ZERO;

        for (Payroll payroll : payrolls) {
            totalEarnings = totalEarnings.add(payroll.getTotalEarnings());
            totalDeductions = totalDeductions.add(payroll.getTotalDeductions());
            totalNetSalary = totalNetSalary.add(payroll.getNetSalary());
        }

        return new MonthlyPaymentDto(
                year,
                month,
                payrolls.size(),
                totalEarnings,
                totalDeductions,
                totalNetSalary
        );
    }
    // API 18: Generate printable payslip text
    //@Transactional(readOnly = true)
    public String getPayslipText(Long id) {
        Payroll p = getPayroll(id);

        return "EASYPAY PAYSLIP\n"
                + "Payroll ID: " + p.getId() + "\n"
                + "Employee ID: " + p.getEmployee().getId() + "\n"
                + "Pay Period: " + p.getPayPeriodStart()
                + " to " + p.getPayPeriodEnd() + "\n"
                + "Basic Salary: " + p.getBasicSalary() + "\n"
                + "Total Earnings: " + p.getTotalEarnings() + "\n"
                + "Total Deductions: " + p.getTotalDeductions() + "\n"
                + "Net Salary: " + p.getNetSalary() + "\n"
                + "Status: " + p.getStatus();
    }

    //@Transactional(readOnly = true)
    public PaymentStatusDto getPaymentStatus(Long id) {
        Payroll payroll = getPayroll(id);
        return PayrollMapper.toPaymentStatusResponse(payroll);
    }

    private Payroll getPayroll(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Payroll not found"));
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("Invalid date range");
        }
    }

    //@Transactional(readOnly = true)
    public List<PayrollRespDto> getMyPayroll(String username) {

        List<Payroll> payrolls =
                payrollRepository.findMyPayroll(username);

        List<PayrollRespDto> result = new ArrayList<>();

        for (Payroll payroll : payrolls) {
            result.add(PayrollMapper.convertEntityToDto(payroll));
        }

        return result;
    }


}
