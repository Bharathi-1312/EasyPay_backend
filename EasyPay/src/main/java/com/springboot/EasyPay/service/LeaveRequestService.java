package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.LeaveRequestDto;
import com.springboot.EasyPay.Dto.request.LeaveReviewDto;
import com.springboot.EasyPay.enums.Status;
import com.springboot.EasyPay.exception.ResourceNotFoundException;
import com.springboot.EasyPay.mapper.LeaveRequestMapper;
import com.springboot.EasyPay.mapper.PayrollMapper;
import com.springboot.EasyPay.model.*;
import com.springboot.EasyPay.repository.EmployeeRepository;
import com.springboot.EasyPay.repository.LeaveRequestRepository;
import com.springboot.EasyPay.repository.ManagerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final ManagerRepository managerRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    public void reqLeaveToManager(String employeeName, @Valid LeaveRequestDto leaveRequestDto) {

        Employee employee = employeeRepository.getByUsername(employeeName);
        LeaveRequest leaveRequest = LeaveRequestMapper.mapDtoToEntity(leaveRequestDto);
        leaveRequest.setEmployee(employee);
        leaveRequest.setManager(employee.getManager());
        leaveRequest.setStatus(Status.PENDING);
        leaveRequestRepository.save(leaveRequest);
    }

    public void reviewLeaveByManager(String managerName,Long leaveId, LeaveReviewDto reviewDto) {

        Manager manager = managerRepository.getByUsername(managerName);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        if (leaveRequest.getStatus() != Status.PENDING) {
            throw new IllegalStateException("This leave request has already been processed.");
        }

        leaveRequest.setStatus(reviewDto.status());
        leaveRequest.setReviewComments(reviewDto.reviewComments());

        leaveRequest.setReviewedAt(LocalDateTime.now());

        leaveRequestRepository.save(leaveRequest);
    }

}
