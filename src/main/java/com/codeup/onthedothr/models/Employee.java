package com.codeup.onthedothr.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="employees")
public class Employee {
    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "supervisor")
    private List<Employee> employees;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
    private List<Deliverable> deliverables;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "supervisor")
    private List<Deliverable> deliverablesCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "employee")
    private List<Appointment> appointments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "supervisor")
    private List<Appointment> createdAppointments;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = true, length = 15)
    private String contactNo;

    @Column(nullable = false)
    private boolean isSupervisor;

    @Column(nullable = false)
    private boolean isAdmin;

    // Constructors
    public Employee(){}

    // Sign-up Constructor (unused right now)
    public Employee(String email, String username, String password, String firstName, String lastName) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Errything (unused right now)
    public Employee(List<Employee> employees, List<Deliverable> deliverables, List<Deliverable> deliverablesCreated,
                    Employee supervisor, List<Appointment> appointments, List<Appointment> createdAppointments,
                    String email, String username, String password, String firstName, String lastName, String contactNo,
                    boolean isSupervisor, boolean isAdmin) {
        this.employees = employees;
        this.deliverables = deliverables;
        this.deliverablesCreated = deliverablesCreated;
        this.supervisor = supervisor;
        this.appointments = appointments;
        this.createdAppointments = createdAppointments;
        this.email = email;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.isSupervisor = isSupervisor;
        this.isAdmin = isAdmin;
    }

    // Copy Constructor
    public Employee(Employee copy){
        id = copy.id;
        email = copy.email;
        username = copy.username;
        password = copy.password;
        firstName = copy.firstName;
        lastName = copy.lastName;
        contactNo = copy.contactNo;
        isSupervisor = copy.isSupervisor;
        isAdmin = copy.isAdmin;
        employees = copy.employees;
        deliverables = copy.deliverables;
        deliverablesCreated = copy.deliverablesCreated;
        supervisor = copy.supervisor;
        appointments = copy.appointments;
        createdAppointments = copy.createdAppointments;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public boolean isSupervisor() {
        return isSupervisor;
    }

    public void setSupervisor(boolean supervisor) {
        isSupervisor = supervisor;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public List<Deliverable> getDeliverables() {
        return deliverables;
    }

    public void setDeliverables(List<Deliverable> deliverables) {
        this.deliverables = deliverables;
    }

    public List<Deliverable> getDeliverablesCreated() {
        return deliverablesCreated;
    }

    public void setDeliverablesCreated(List<Deliverable> deliverablesCreated) {
        this.deliverablesCreated = deliverablesCreated;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Appointment> getCreatedAppointments() {
        return createdAppointments;
    }

    public void setCreatedAppointments(List<Appointment> createdAppointments) {
        this.createdAppointments = createdAppointments;
    }
}