package com.org.EmployeeManagementSystem.service;

import com.org.EmployeeManagementSystem.model.Employee;
import com.org.EmployeeManagementSystem.repository.EmployeeRepository;
import com.org.EmployeeManagementSystem.entity.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.org.EmployeeManagementSystem.exception.ResourceNotFoundException;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll().stream()
            .map(this::convertToModel)
            .collect(Collectors.toList());
    }

    private Employee convertToModel(EmployeeEntity entity) {
        return new Employee(
            entity.getEmployeeId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getHireDate(),
            entity.getJobTitle(),
            entity.getSalary()
        );
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
            .map(this::convertToModel)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee createEmployee(Employee employee) {
        // Implement the logic to create an employee
        EmployeeEntity entityToSave = convertToEntity(employee);
        EmployeeEntity savedEntity = employeeRepository.save(entityToSave);
        return convertToModel(savedEntity);
    }

    private EmployeeEntity convertToEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setEmployeeId(employee.getEmployeeId());
        entity.setFirstName(employee.getFirstName());
        entity.setLastName(employee.getLastName());
        entity.setEmail(employee.getEmail());
        entity.setHireDate(employee.getHireDate());
        entity.setJobTitle(employee.getJobTitle());
        entity.setSalary(employee.getSalary());
        return entity;
    }

    public Employee updateEmployee(Integer id, Employee employee) {
        EmployeeEntity existingEntity = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        // Update the existing entity with the new data
        existingEntity.setFirstName(employee.getFirstName());
        existingEntity.setLastName(employee.getLastName());
        existingEntity.setEmail(employee.getEmail());
        existingEntity.setHireDate(employee.getHireDate());
        existingEntity.setJobTitle(employee.getJobTitle());
        existingEntity.setSalary(employee.getSalary());
        
        // Save the updated entity
        EmployeeEntity updatedEntity = employeeRepository.save(existingEntity);
        
        // Convert and return the updated employee
        return convertToModel(updatedEntity);
    }

    public void deleteEmployee(Integer id) {
        EmployeeEntity employee = employeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        
        employeeRepository.delete(employee);
    }

}
