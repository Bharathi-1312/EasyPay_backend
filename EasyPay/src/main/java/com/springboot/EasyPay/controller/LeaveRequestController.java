package com.springboot.EasyPay.controller;

import com.springboot.EasyPay.Dto.request.LeaveRequestDto;
import com.springboot.EasyPay.Dto.request.LeaveReviewDto;
import com.springboot.EasyPay.Dto.request.PayrollDto;
import com.springboot.EasyPay.model.LeaveRequest;
import com.springboot.EasyPay.service.LeaveRequestService;
import com.springboot.EasyPay.service.PayrollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping("api/leavereq/apply")
    public void reqLeaveToManager(Principal principal,
                                  @Valid @RequestBody LeaveRequestDto leaveRequestDto) {
        leaveRequestService.reqLeaveToManager( principal.getName(),leaveRequestDto);
    }

    @PutMapping("api/reviewleave/{leaveId}")
    public void reviewLeaveByManager(
         Principal principal,
            @PathVariable Long leaveId,
            @Valid @RequestBody LeaveReviewDto leaveReviewDto
    ){
        leaveRequestService.reviewLeaveByManager(principal.getName(),leaveId, leaveReviewDto);
    }


}
