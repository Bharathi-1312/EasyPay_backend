package com.springboot.EasyPay.model;

import com.springboot.EasyPay.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Getter
@Setter
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate payPeriodStart;

    private LocalDate payPeriodEnd;


    private double basicSalary;


    private double totalEarnings;


    private double totalDeductions;


    private double netSalary;

    @Enumerated(EnumType.STRING)
    private Status status; // DRAFT, PROCESSED, PAID

    private LocalDateTime processedAt;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "HR_id")
    private HR hr;

    @OneToMany(mappedBy = "payroll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PayrollComponents> components = new ArrayList<>();

    public void addComponent(PayrollComponents component) {
        component.setPayroll(this);
        components.add(component);
    }

}
