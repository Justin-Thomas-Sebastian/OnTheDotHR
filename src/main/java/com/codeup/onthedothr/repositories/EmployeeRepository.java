package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUsername(String username);

    @Query(value = "SELECT * FROM employees WHERE supervisor_id = ?1", nativeQuery = true)
    List<Employee> findAssignedEmployees(long supervisorId);
}
