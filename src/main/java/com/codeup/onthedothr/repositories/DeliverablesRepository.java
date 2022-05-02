package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliverablesRepository extends JpaRepository <Deliverable, Long> {
    @Query(value = "SELECT * FROM deliverables WHERE employee_id = ?1", nativeQuery = true)
    List<Deliverable> findDeliverablesById(long employeeId);
}