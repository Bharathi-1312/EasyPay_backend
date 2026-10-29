package com.springboot.EasyPay.repository;

import com.springboot.EasyPay.Dto.response.PayrollRespDto;
import com.springboot.EasyPay.enums.PayrollStatus;
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




    // API 2: Filter payrolls by status
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.status = ?1
        """)
    List<Payroll> findByStatus(
             PayrollStatus status);

    // API 3: Payroll periods completely inside the requested range
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.payPeriodStart >= ?1
          AND p.payPeriodEnd <= ?2
        ORDER BY p.payPeriodStart DESC
        """)
    List<Payroll> findByPeriod(
             LocalDate start,
            LocalDate end);

    // API 4: Employee payrolls whose start date is in the range
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.employee.id = ?1
          AND p.payPeriodStart BETWEEN ?2 AND ?3
        ORDER BY p.payPeriodStart DESC
        """)
    List<Payroll> searchPayroll(
             Long employeeId,
            LocalDate start,
             LocalDate end);

    // API 7 and 8: Employee payroll history
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.employee.id = ?1
        ORDER BY p.payPeriodStart DESC
        """)
    List<Payroll> findEmployeePayrollHistory(
            Long employeeId);


    // Monthly report: payroll periods overlapping the requested month
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.payPeriodStart <= ?2
          AND p.payPeriodEnd >= ?1
        ORDER BY p.payPeriodStart
        """)
    List<Payroll> findPayrollsOverlappingPeriod(
            LocalDate monthStart,
             LocalDate monthEnd);

    // Find payroll records belonging to a particular HR
    @Query("""
        SELECT p
        FROM Payroll p
        WHERE p.hr.id = ?1
        ORDER BY p.payPeriodStart DESC
        """)
    List<Payroll> findPayrollsProcessedByHr(
            Long hrId);


    @Query("""
    SELECT p
    FROM Payroll p
    WHERE p.employee.user.username = ?1
    ORDER BY p.payPeriodStart DESC
    """)
    List<Payroll> findMyPayroll(
           String username);




}


