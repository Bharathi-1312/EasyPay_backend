package com.springboot.EasyPay.mapper;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.model.PayrollComponents;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class PayrollComponentMapper {

    public static PayrollComponents mapDtoToEntity(@Valid PayrollComponentDto dto) {
        PayrollComponents entity = new PayrollComponents();
        entity.setComponentName(dto.componentName());
        entity.setComponentType(dto.componentType());
        entity.setAmount(dto.amount());
        entity.setDescription(dto.description());
        return entity;
    }
}
