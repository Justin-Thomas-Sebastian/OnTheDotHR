package com.codeup.onthedothr.repositories;

import com.codeup.onthedothr.models.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentStatusRepository extends JpaRepository <AppointmentStatus, Long> {
}