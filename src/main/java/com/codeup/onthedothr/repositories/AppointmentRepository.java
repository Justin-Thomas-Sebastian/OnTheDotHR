package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository <Appointment, Long> {
    @Query(value = "SELECT * FROM appointments WHERE supervisor_id = ?1 AND status_id = ?2", nativeQuery = true)
    List<Appointment> findAppointmentBySupervisorIdAndStatusId(long supervisorId, long statusId);

    @Query(value = "SELECT * FROM appointments WHERE employee_id = ?1 AND status_id = ?2", nativeQuery = true)
    List<Appointment> findAppointmentByUserIdAndStatusId(long employeeId, long statusId);
}