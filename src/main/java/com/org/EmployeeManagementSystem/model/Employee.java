package com.org.EmployeeManagementSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private Integer employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate hireDate;
    private String jobTitle;
    private BigDecimal salary;
}
