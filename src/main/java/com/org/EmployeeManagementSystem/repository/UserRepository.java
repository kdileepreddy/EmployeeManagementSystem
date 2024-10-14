package com.org.EmployeeManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.EmployeeManagementSystem.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);                         
    UserEntity findByEmail(String email);
}
