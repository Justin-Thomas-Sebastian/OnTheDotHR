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
    @JoinColumn (name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private Employee supervisor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "deliverable")
    private List<DeliverableAttachments> attachments;

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
}