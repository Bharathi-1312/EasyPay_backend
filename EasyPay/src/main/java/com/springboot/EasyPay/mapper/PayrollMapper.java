package com.springboot.EasyPay.mapper;

import com.springboot.EasyPay.Dto.request.EmployeeDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.Payroll;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {
    public static Payroll mapDtoToEntity(@Valid PayrollDto dto) {
        Payroll payroll=new Payroll();
        payroll.setPayPeriodStart(dto.payPeriodStart());
        payroll.setPayPeriodEnd(dto.payPeriodEnd());
        payroll.setBasicSalary(dto.basicSalary());
        payroll.setTotalEarnings(dto.totalEarnings());
        payroll.setTotalDeductions(dto.totalDeductions());
        payroll.setNetSalary(dto.netSalary());
        payroll.setStatus(dto.status());
        return payroll;
    }
}
