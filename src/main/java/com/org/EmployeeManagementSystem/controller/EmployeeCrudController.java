package com.org.EmployeeManagementSystem.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.org.EmployeeManagementSystem.service.EmployeeService;
import com.org.EmployeeManagementSystem.model.Employee;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/")
@Tag(name = "Employee Management", description = "APIs for managing employees")
@CrossOrigin(origins = "*")
public class EmployeeCrudController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of employees",
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = Employee.class)))),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token")
    })
    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token) {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @Operation(summary = "Get an employee by ID", description = "Retrieves an employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee",
                     content = @Content(schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token")
    })
    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployeeById(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token,
                                                    @Parameter(description = "ID of the employee to retrieve") @PathVariable Integer id) {
        Employee employee = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Operation(summary = "Create a new employee", description = "Creates a new employee record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee successfully created",
                     content = @Content(schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token")
    })
    @PostMapping("/registerEmployee")
    public ResponseEntity<Employee> createEmployee(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token,
                                                   @RequestBody Employee employee) {
        Employee createdEmployee = employeeService.createEmployee(employee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an employee", description = "Updates an existing employee record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee successfully updated",
                     content = @Content(schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token")
    })
    @PutMapping("/updateEmployee/{id}")
    public ResponseEntity<Employee> updateEmployee(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token,
                                                   @Parameter(description = "ID of the employee to update") @PathVariable Integer id,
                                                   @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
    }

    @Operation(summary = "Delete an employee", description = "Deletes an employee record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or missing token")
    })
    @DeleteMapping("/deleteEmployee/{id}")
    public ResponseEntity<Void> deleteEmployee(@Parameter(description = "JWT token", required = true) @RequestHeader("Authorization") String token,
                                               @Parameter(description = "ID of the employee to delete") @PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
