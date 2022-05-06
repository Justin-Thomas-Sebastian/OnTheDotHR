package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUsername(String username);

    @Query(value = "SELECT * FROM employees WHERE supervisor_id = ?1", nativeQuery = true)
    List<Employee> findAssignedEmployees(long supervisorId);

    @Query(value = "SELECT supervisor_id FROM employees WHERE id = ?1", nativeQuery = true)
    Long getSupervisorIdById(Long id);

    @Query(value = "SELECT * FROM employees WHERE first_name LIKE %:name% OR last_name LIKE %:name%", nativeQuery = true)
    List<Employee> findEmployeesByName(@Param("name") String name);

    @Query(value = "SELECT * FROM employees WHERE email LIKE %:email%", nativeQuery = true)
    List<Employee> findEmployeesByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM employees WHERE username LIKE %:username%", nativeQuery = true)
    List<Employee> findEmployeesByUsername(@Param("username") String username);
}