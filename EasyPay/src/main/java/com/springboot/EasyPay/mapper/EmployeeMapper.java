package com.springboot.EasyPay.mapper;

import com.springboot.EasyPay.Dto.request.EmployeeDto;
import com.springboot.EasyPay.model.Employee;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public static Employee mapDtoToEntity(@Valid EmployeeDto employeeDto) {
        Employee employee = new Employee();
        employee.setFullName(employeeDto.fullName());
        employee.setEmail(employeeDto.email());
        employee.setPhone(employeeDto.phone());
        employee.setAddress(employeeDto.address());
        employee.setDesignation(employeeDto.designation());
        employee.setDepartment(employeeDto.department());
        employee.setJoiningDate(employeeDto.joiningDate());
        employee.setBasicSalary(employeeDto.basicSalary());
        return employee;
    }
}
