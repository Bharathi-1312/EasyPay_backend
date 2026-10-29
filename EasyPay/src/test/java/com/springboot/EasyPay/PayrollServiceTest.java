package com.springboot.EasyPay;

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
import com.springboot.EasyPay.repository.PayrollRepository;
import com.springboot.EasyPay.repository.PayrollComponentRepository;
import com.springboot.EasyPay.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollServiceTest {

    @InjectMocks
    private PayrollService payrollService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private HRRepository hrRepository;

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PayrollComponentRepository componentRepository;

    @Test
    public void createPayrollTest() {
        PayrollDto dto = mock(PayrollDto.class);
        Employee employee = new Employee();
        Payroll payroll = new Payroll();
        PayrollRespDto response = mock(PayrollRespDto.class);

        when(dto.basicSalary()).thenReturn(new BigDecimal("50000"));
        when(dto.payPeriodStart()).thenReturn(LocalDate.of(2026, 10, 1));
        when(dto.payPeriodEnd()).thenReturn(LocalDate.of(2026, 10, 31));
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));
        when(payrollRepository
                .existsByEmployee_IdAndPayPeriodStartAndPayPeriodEnd(
                        eq(1L), any(), any())).thenReturn(false);
        when(payrollRepository.save(any(Payroll.class)))
                .thenReturn(payroll);

        try (MockedStatic<PayrollMapper> mapper =
                     mockStatic(PayrollMapper.class)) {

            mapper.when(() -> PayrollMapper.convertDtoToEntity(dto))
                    .thenReturn(payroll);
            mapper.when(() -> PayrollMapper.convertEntityToDto(payroll))
                    .thenReturn(response);

            PayrollRespDto result = payrollService.createPayroll(1L, dto);

            assertSame(response, result);
            assertSame(employee, payroll.getEmployee());
            assertEquals(BigDecimal.ZERO, payroll.getTotalEarnings());
            assertEquals(BigDecimal.ZERO, payroll.getTotalDeductions());
            assertEquals(BigDecimal.ZERO, payroll.getNetSalary());
            assertEquals(PayrollStatus.DRAFT, payroll.getStatus());

            verify(payrollRepository).save(payroll);
        }
    }

    @Test
    public void createPayrollTest_NegativeSalary() {
        PayrollDto dto = mock(PayrollDto.class);
        when(dto.basicSalary()).thenReturn(new BigDecimal("-100"));

        assertThrows(IllegalArgumentException.class,
                () -> payrollService.createPayroll(1L, dto));

        verify(employeeRepository, never()).findById(anyLong());
    }

    @Test
    public void createPayrollTest_InvalidPeriod() {
        PayrollDto dto = mock(PayrollDto.class);
        when(dto.basicSalary()).thenReturn(new BigDecimal("50000"));
        when(dto.payPeriodStart()).thenReturn(LocalDate.of(2026, 10, 31));
        when(dto.payPeriodEnd()).thenReturn(LocalDate.of(2026, 10, 1));

        assertThrows(IllegalArgumentException.class,
                () -> payrollService.createPayroll(1L, dto));

        verify(employeeRepository, never()).findById(anyLong());
    }

    @Test
    public void createPayrollTest_EmployeeNotFound() {
        PayrollDto dto = mock(PayrollDto.class);
        when(dto.basicSalary()).thenReturn(new BigDecimal("50000"));
        when(dto.payPeriodStart()).thenReturn(LocalDate.of(2026, 10, 1));
        when(dto.payPeriodEnd()).thenReturn(LocalDate.of(2026, 10, 31));
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> payrollService.createPayroll(1L, dto));

        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    public void createPayrollTest_Duplicate() {
        PayrollDto dto = mock(PayrollDto.class);
        when(dto.basicSalary()).thenReturn(new BigDecimal("50000"));
        when(dto.payPeriodStart()).thenReturn(LocalDate.of(2026, 10, 1));
        when(dto.payPeriodEnd()).thenReturn(LocalDate.of(2026, 10, 31));
        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(new Employee()));
        when(payrollRepository
                .existsByEmployee_IdAndPayPeriodStartAndPayPeriodEnd(
                        eq(1L), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> payrollService.createPayroll(1L, dto));

        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    public void processPayrollTest() {
        Payroll payroll = new Payroll();
        payroll.setBasicSalary(new BigDecimal("50000"));
        payroll.setStatus(PayrollStatus.DRAFT);

        PayrollComponent earning = new PayrollComponent();
        earning.setAmountPercentage(new BigDecimal("10"));
        earning.setComponentType(ComponentType.EARNING);

        PayrollComponent deduction = new PayrollComponent();
        deduction.setAmountPercentage(new BigDecimal("5"));
        deduction.setComponentType(ComponentType.DEDUCTION);

        PayrollRespDto response = mock(PayrollRespDto.class);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));
        when(hrRepository.getByUsername("hr1")).thenReturn(new HR());
        when(componentRepository.findByPayroll_Id(1L))
                .thenReturn(List.of(earning, deduction));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        try (MockedStatic<PayrollMapper> mapper =
                     mockStatic(PayrollMapper.class)) {

            mapper.when(() -> PayrollMapper.convertEntityToDto(payroll))
                    .thenReturn(response);

            PayrollRespDto result =
                    payrollService.processPayroll(1L, "hr1");

            assertSame(response, result);
            assertEquals(new BigDecimal("55000.0000"),
                    payroll.getTotalEarnings());
            assertEquals(new BigDecimal("2500.0000"),
                    payroll.getTotalDeductions());
            assertEquals(new BigDecimal("52500.0000"),
                    payroll.getNetSalary());
            assertEquals(PayrollStatus.PROCESSED, payroll.getStatus());
            assertNotNull(payroll.getProcessedAt());
            assertNotNull(payroll.getHr());

            verify(payrollRepository).save(payroll);
        }
    }

    @Test
    public void processPayrollTest_NotFound() {
        when(payrollRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> payrollService.processPayroll(99L, "hr1"));

        verify(componentRepository, never()).findByPayroll_Id(99L);
    }

    @Test
    public void processPayrollTest_NotDraft() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.PROCESSED);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));

        assertThrows(IllegalStateException.class,
                () -> payrollService.processPayroll(1L, "hr1"));

        verify(hrRepository, never()).getByUsername("hr1");
    }

    @Test
    public void processPayrollTest_NegativeNetSalary() {
        Payroll payroll = new Payroll();
        payroll.setBasicSalary(new BigDecimal("100"));
        payroll.setStatus(PayrollStatus.DRAFT);

        PayrollComponent deduction = new PayrollComponent();
        deduction.setAmountPercentage(new BigDecimal("200"));
        deduction.setComponentType(ComponentType.DEDUCTION);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));
        when(hrRepository.getByUsername("hr1")).thenReturn(new HR());
        when(componentRepository.findByPayroll_Id(1L))
                .thenReturn(List.of(deduction));

        assertThrows(IllegalStateException.class,
                () -> payrollService.processPayroll(1L, "hr1"));

        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    public void markAsPaidTest() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.PROCESSED);
        PayrollRespDto response = mock(PayrollRespDto.class);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));
        when(payrollRepository.save(payroll)).thenReturn(payroll);

        try (MockedStatic<PayrollMapper> mapper =
                     mockStatic(PayrollMapper.class)) {

            mapper.when(() -> PayrollMapper.convertEntityToDto(payroll))
                    .thenReturn(response);

            assertSame(response, payrollService.markAsPaid(1L));
            assertEquals(PayrollStatus.PAID, payroll.getStatus());

            verify(payrollRepository).save(payroll);
        }
    }

    @Test
    public void markAsPaidTest_NotProcessed() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.DRAFT);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));

        assertThrows(IllegalStateException.class,
                () -> payrollService.markAsPaid(1L));

        verify(payrollRepository, never()).save(any(Payroll.class));
    }

    @Test
    public void getPayslipTest() {
        Payroll payroll = new Payroll();
        PayrollRespDto response = mock(PayrollRespDto.class);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));

        try (MockedStatic<PayrollMapper> mapper =
                     mockStatic(PayrollMapper.class)) {

            mapper.when(() -> PayrollMapper.convertEntityToDto(payroll))
                    .thenReturn(response);

            assertSame(response, payrollService.getPayslip(1L));
        }
    }

    @Test
    public void getPayslipTest_NotFound() {
        when(payrollRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> payrollService.getPayslip(99L));
    }

    @Test
    public void getEmployeePayslipsTest() {
        List<PayrollRespDto> payslips =
                List.of(mock(PayrollRespDto.class));

        when(payrollRepository.getPayrollByEmployeeId(
                1L, PageRequest.of(0, 5))).thenReturn(payslips);

        List<PayrollRespDto> result =
                payrollService.getEmployeePayslips(
                        1L, PageRequest.of(0, 5));

        assertEquals(1, result.size());
        verify(payrollRepository).getPayrollByEmployeeId(
                1L, PageRequest.of(0, 5));
    }
}