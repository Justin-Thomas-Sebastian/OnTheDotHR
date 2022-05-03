package com.codeup.onthedothr.models;

import javax.persistence.*;
import java.util.List;
import java.util.Date;

@Entity
@Table(name = "deliverables")
public class Deliverable {
    //Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn (name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Employee supervisor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deliverable")
    private List<DeliverableAttachment> attachments;

    @OneToOne
    private Category category;

    @OneToOne
    private Status status;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateCreated;

    @Column
    @Temporal(TemporalType.DATE)
    private Date lastActive;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date deadline;

    // Constructors
    public Deliverable(){}

    public Deliverable(Employee employee, Employee supervisor, List<DeliverableAttachment> attachments, Category category, Status status, String title, String description, Date dateCreated, Date lastActive, Date deadline) {
        this.employee = employee;
        this.supervisor = supervisor;
        this.attachments = attachments;
        this.category = category;
        this.status = status;
        this.title = title;
        this.description = description;
        this.dateCreated = dateCreated;
        this.lastActive = lastActive;
        this.deadline = deadline;
    }

    // Getter and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Employee supervisor) {
        this.supervisor = supervisor;
    }

    public List<DeliverableAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DeliverableAttachment> attachments) {
        this.attachments = attachments;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}