package com.org.EmployeeManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.org.EmployeeManagementSystem.entity.EmployeeEntity;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    
}
