
CREATE DATABASE IF NOT EXISTS easypay_db;
USE easypay_db;

-- 1. USERS TABLE
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('HR', 'MANAGER', 'EMPLOYEE') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. HR TABLE
CREATE TABLE hr (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department VARCHAR(100),
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_hr_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 3. MANAGER TABLE
CREATE TABLE manager (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department VARCHAR(100),
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_manager_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 4. EMPLOYEE TABLE
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address varchar(100),
    department ENUM('IT','FINANCE','MANAGEMENT','SALES','SUPPORT') NOT NULL,
    designation ENUM('HR', 'DEVELOPER', 'MANAGER','SALES EXECUTIVE','ANALYST') NOT NULL,
    joining_date DATE,
    basic_salary DECIMAL(12,2) NOT NULL,
    user_id BIGINT UNIQUE,
    manager_id BIGINT,
    created_by_hr_id BIGINT,
    CONSTRAINT fk_employee_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_employee_manager
        FOREIGN KEY (manager_id) REFERENCES manager(id),
    CONSTRAINT fk_employee_hr
        FOREIGN KEY (created_by_hr_id) REFERENCES hr(id)
);

-- 5. PAYROLL TABLE
CREATE TABLE payroll (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    total_earnings DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_deductions DECIMAL(12,2) NOT NULL DEFAULT 0,
    net_salary DECIMAL(12,2) NOT NULL DEFAULT 0,
    status ENUM('DRAFT', 'PROCESSED', 'PAID')
        NOT NULL DEFAULT 'DRAFT',
    processed_at DATETIME,
    processed_by_hr_id BIGINT,
    CONSTRAINT fk_payroll_employee
        FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT fk_payroll_hr
        FOREIGN KEY (processed_by_hr_id) REFERENCES hr(id),
    CONSTRAINT uq_employee_pay_period
        UNIQUE (employee_id, pay_period_start, pay_period_end)
);

-- 6. PAYROLL COMPONENTS TABLE
CREATE TABLE payroll_components (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payroll_id BIGINT NOT NULL,
    component_name ENUM('BASIC_PAY','HRA','BONUS','TAX','PF') NOT NULL,
    component_type ENUM('EARNING', 'DEDUCTION') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT fk_component_payroll
        FOREIGN KEY (payroll_id) REFERENCES payroll(id)
);

-- 7. LEAVE REQUEST TABLE
CREATE TABLE leave_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    manager_id BIGINT,
    leave_type ENUM('CASUAL', 'SICK', 'EARNED') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED')
        NOT NULL DEFAULT 'PENDING',
    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME,
    review_comments VARCHAR(500),
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT fk_leave_manager
        FOREIGN KEY (manager_id) REFERENCES manager(id)
);



