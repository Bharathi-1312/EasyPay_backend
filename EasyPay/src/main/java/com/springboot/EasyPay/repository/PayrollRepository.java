package com.springboot.EasyPay.repository;

import com.springboot.EasyPay.Dto.response.PayrollRespDto;
import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Payroll;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository
        extends JpaRepository<Payroll, Long> {

    @Query("""
            select p.id,
                e.id,
                p.payPeriodStart,
                p.payPeriodEnd,
                p.basicSalary,
                p.totalEarnings,
                p.totalDeductions,
                p.netSalary,
                p.status,
                p.processedAt
            from Payroll p
            join p.employee e
            where e.id = ?1
            order by p.payPeriodStart desc
            """)
    List<PayrollRespDto> getPayrollByEmployeeId(
            @Param("employeeId") Long employeeId,
            Pageable pageable
    );

    boolean existsByEmployee_IdAndPayPeriodStartAndPayPeriodEnd(
            Long employeeId,
            LocalDate payPeriodStart,
            LocalDate payPeriodEnd
    );

    Optional<Payroll> findById(Long id);
}
