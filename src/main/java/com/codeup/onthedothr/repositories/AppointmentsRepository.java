package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentsRepository extends JpaRepository <Appointment, Long> {
}