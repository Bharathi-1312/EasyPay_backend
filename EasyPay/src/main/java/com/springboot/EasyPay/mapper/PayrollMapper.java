package com.springboot.EasyPay.mapper;

import com.springboot.EasyPay.Dto.request.EmployeeDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.Dto.response.PayrollRespDto;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.Payroll;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {

    public static Payroll convertDtoToEntity(PayrollDto dto) {
        Payroll payroll = new Payroll();

        payroll.setPayPeriodStart(dto.payPeriodStart());
        payroll.setPayPeriodEnd(dto.payPeriodEnd());
        payroll.setBasicSalary(dto.basicSalary());

        return payroll;
    }

    public static PayrollRespDto convertEntityToDto(Payroll payroll) {
        return new PayrollRespDto(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getPayPeriodStart(),
                payroll.getPayPeriodEnd(),
                payroll.getBasicSalary(),
                payroll.getTotalEarnings(),
                payroll.getTotalDeductions(),
                payroll.getNetSalary(),
                payroll.getStatus(),
                payroll.getProcessedAt()
        );
    }
}
