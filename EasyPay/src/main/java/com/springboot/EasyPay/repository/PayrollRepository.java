package com.springboot.EasyPay.repository;

import com.springboot.EasyPay.model.HR;
import com.springboot.EasyPay.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollRepository  extends JpaRepository<Payroll,Long> {

}
