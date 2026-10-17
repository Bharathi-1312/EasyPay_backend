package com.springboot.EasyPay.repository;

import com.springboot.EasyPay.model.PayrollComponents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollComponentRepository extends JpaRepository<PayrollComponents,Long> {
}
