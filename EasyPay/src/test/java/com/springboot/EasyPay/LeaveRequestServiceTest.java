package com.springboot.EasyPay;

import com.springboot.EasyPay.Dto.request.LeaveRequestDto;
import com.springboot.EasyPay.Dto.request.LeaveReviewDto;
import com.springboot.EasyPay.enums.Status;
import com.springboot.EasyPay.exception.ResourceNotFoundException;
import com.springboot.EasyPay.mapper.LeaveRequestMapper;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.LeaveRequest;
import com.springboot.EasyPay.model.Manager;
import com.springboot.EasyPay.repository.EmployeeRepository;
import com.springboot.EasyPay.repository.LeaveRequestRepository;
import com.springboot.EasyPay.repository.ManagerRepository;
import com.springboot.EasyPay.service.LeaveRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class LeaveRequestServiceTest {

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    public void reqLeaveToManagerTest() {
        Employee employee = new Employee();
        Manager manager = new Manager();
        employee.setManager(manager);

        LeaveRequestDto dto = mock(LeaveRequestDto.class);
        LeaveRequest leaveRequest = new LeaveRequest();

        when(employeeRepository.getByUsername("employee1"))
                .thenReturn(employee);

        try (MockedStatic<LeaveRequestMapper> mapper =
                     mockStatic(LeaveRequestMapper.class)) {

            mapper.when(() -> LeaveRequestMapper.mapDtoToEntity(dto))
                    .thenReturn(leaveRequest);

            leaveRequestService.reqLeaveToManager("employee1", dto);

            assertSame(employee, leaveRequest.getEmployee());
            assertSame(manager, leaveRequest.getManager());
            assertEquals(Status.PENDING, leaveRequest.getStatus());

            verify(leaveRequestRepository).save(leaveRequest);
        }
    }

    @Test
    public void reviewLeaveByManagerTest() {
        Manager manager = new Manager();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStatus(Status.PENDING);

        LeaveReviewDto dto = mock(LeaveReviewDto.class);
        when(dto.status()).thenReturn(Status.APPROVED);
        when(dto.reviewComments()).thenReturn("Approved");

        when(managerRepository.getByUsername("manager1"))
                .thenReturn(manager);
        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(leaveRequest));

        leaveRequestService.reviewLeaveByManager("manager1", 1L, dto);

        assertEquals(Status.APPROVED, leaveRequest.getStatus());
        assertEquals("Approved", leaveRequest.getReviewComments());
        assertNotNull(leaveRequest.getReviewedAt());

        verify(leaveRequestRepository).save(leaveRequest);
    }

    @Test
    public void reviewLeaveByManagerTest_NotFound() {
        when(managerRepository.getByUsername("manager1"))
                .thenReturn(new Manager());
        when(leaveRequestRepository.findById(99L))
                .thenReturn(Optional.empty());

        LeaveReviewDto dto = mock(LeaveReviewDto.class);

        assertThrows(ResourceNotFoundException.class,
                () -> leaveRequestService.reviewLeaveByManager(
                        "manager1", 99L, dto));

        verify(leaveRequestRepository, never())
                .save(any(LeaveRequest.class));
    }

    @Test
    public void reviewLeaveByManagerTest_AlreadyProcessed() {
        Manager manager = new Manager();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStatus(Status.APPROVED);

        when(managerRepository.getByUsername("manager1"))
                .thenReturn(manager);
        when(leaveRequestRepository.findById(1L))
                .thenReturn(Optional.of(leaveRequest));

        LeaveReviewDto dto = mock(LeaveReviewDto.class);

        assertThrows(IllegalStateException.class,
                () -> leaveRequestService.reviewLeaveByManager(
                        "manager1", 1L, dto));

        verify(leaveRequestRepository, never())
                .save(any(LeaveRequest.class));
    }
}