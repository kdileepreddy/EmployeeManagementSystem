package com.org.EmployeeManagementSystem.repository;

import com.org.EmployeeManagementSystem.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    // Repository methods...
}


