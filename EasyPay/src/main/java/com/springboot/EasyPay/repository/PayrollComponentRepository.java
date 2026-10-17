package com.springboot.EasyPay.repository;

import com.springboot.EasyPay.Dto.response.PayrollComponentRespDto;
import com.springboot.EasyPay.model.PayrollComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollComponentRepository
        extends JpaRepository<PayrollComponent, Long> {

        @Query("""
            select new com.springboot.EasyPay.Dto.response.PayrollComponentRespDto(
                c.id,
                p.id,
                c.componentName ,
                c.componentType,
                c.amount,
                c.description
            )
            from PayrollComponent c
            join c.payroll p
            where p.id = ?1
            order by c.id
            """)
        List<PayrollComponentRespDto> getComponentsByPayrollId(
                @Param("payrollId") Long payrollId
        );

        List<PayrollComponent> findByPayroll_Id(Long payrollId);
}