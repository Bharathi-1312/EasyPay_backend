package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.Dto.response.PayrollComponentRespDto;
import com.springboot.EasyPay.enums.PayrollStatus;
import com.springboot.EasyPay.mapper.PayrollComponentMapper;
import com.springboot.EasyPay.model.Payroll;
import com.springboot.EasyPay.model.PayrollComponent;
import com.springboot.EasyPay.repository.PayrollComponentRepository;
import com.springboot.EasyPay.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollComponentService {

    private final PayrollComponentRepository componentRepository;
    private final PayrollRepository payrollRepository;

    //@Transactional
    public PayrollComponentRespDto addComponent(Long payrollId,
            PayrollComponentDto dto) {

        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new RuntimeException("Payroll not found"));

        if (payroll.getStatus() != PayrollStatus.DRAFT) {
            throw new IllegalStateException(
                    "Components can only be added to draft payroll");
        }

        if (dto.amount() == null
                || dto.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Amount must be zero or greater");
        }

        PayrollComponent component =
                PayrollComponentMapper.convertDtoToEntity(dto);

        component.setPayroll(payroll);

        PayrollComponent saved =
                componentRepository.save(component);

        return PayrollComponentMapper.convertEntityToDto(saved);
    }

    //@Transactional(readOnly = true)
    public List<PayrollComponentRespDto> getComponents(
            Long payrollId) {

        if (!payrollRepository.existsById(payrollId)) {
            throw new RuntimeException("Payroll not found");
        }

        return componentRepository.findByPayroll_Id(payrollId)
                .stream()
                .map(PayrollComponentMapper::convertEntityToDto)
                .toList();
    }

    //@Transactional
    public void deleteComponent(Long componentId) {

        PayrollComponent component =
                componentRepository.findById(componentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payroll component not found"));

        if (component.getPayroll().getStatus()
                != PayrollStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft payroll components can be deleted");
        }

        componentRepository.delete(component);
    }
}
