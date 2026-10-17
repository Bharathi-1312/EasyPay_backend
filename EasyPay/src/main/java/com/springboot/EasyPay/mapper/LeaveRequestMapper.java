package com.springboot.EasyPay.mapper;

import com.springboot.EasyPay.Dto.request.LeaveRequestDto;
import com.springboot.EasyPay.Dto.request.ManagerDto;
import com.springboot.EasyPay.enums.Status;
import com.springboot.EasyPay.model.LeaveRequest;
import com.springboot.EasyPay.model.Manager;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {
    public static LeaveRequest mapDtoToEntity(@Valid LeaveRequestDto dto) {
        LeaveRequest entity = new LeaveRequest();
        entity.setLeaveType(dto.leaveType());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setReason(dto.reason());
        return entity;
    }
}
