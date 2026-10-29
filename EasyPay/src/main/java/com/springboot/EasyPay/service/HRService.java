package com.springboot.EasyPay.service;

import com.springboot.EasyPay.Dto.request.HRDto;
import com.springboot.EasyPay.Dto.request.HRUpdateDto;
import com.springboot.EasyPay.enums.Role;
import com.springboot.EasyPay.exception.InvalidCallException;
import com.springboot.EasyPay.exception.ResourceNotFoundException;
import com.springboot.EasyPay.mapper.HRMapper;
import com.springboot.EasyPay.mapper.ManagerMapper;
import com.springboot.EasyPay.model.Employee;
import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Manager;
import com.springboot.EasyPay.model.User;
import com.springboot.EasyPay.repository.EmployeeRepository;
import com.springboot.EasyPay.repository.HRRepository;
import com.springboot.EasyPay.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HRService {

    private final UserRepository userRepository;
    private final HRRepository hrRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeRepository employeeRepository;

    public void insertHR( HRDto hrDto) {

        User user = new User();
        user.setUsername(hrDto.username());
        user.setPassword(passwordEncoder.encode(hrDto.password()));
        user.setRole(Role.HR);
        user = userRepository.save(user); //<-- This user now has an id attached

        // Step 2: Map Dto to Entity
        HR hr = HRMapper.mapDtoToEntity(hrDto);

        // Step 3: Attach User to Manager
        hr.setUser(user);

        hrRepository.save(hr);
    }

    public HR getById(long id) {

        Optional<HR> optional= hrRepository.findById(id);
        if(optional.isEmpty())
        {
            throw new ResourceNotFoundException("Invalid id");
        }
        return optional.get();
    }

    public void update(String hrName, HRUpdateDto dto) {

        HR hr =  hrRepository.getByUsername(hrName);
        boolean flag = false;

        if(!(dto.name() == null) && !(dto.name().trim().isEmpty())) {    // If dto.name() is NOT(!) empty
            hr.setName(dto.name());
            flag = true;
        }

        if(!(dto.phone() == null) && !(dto.phone().trim().isEmpty())) {    // If dto.name() is NOT(!) empty
            hr.setPhone(dto.phone());
            flag = true;
        }

        if(!(dto.address() == null) && !(dto.address().trim().isEmpty())) {
            hr.setPhone(dto.phone());
            flag = true;
        }

        if(!flag)
            throw new InvalidCallException("Update op terminated");

        hrRepository.save(hr);
    }
}
