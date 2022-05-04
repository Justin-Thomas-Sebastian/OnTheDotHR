package com.codeup.onthedothr.models;

import javax.persistence.*;

@Entity
@Table(name="appointmentStatus")
public class AppointmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String statusName;

    public AppointmentStatus(){}

    public AppointmentStatus(long id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
