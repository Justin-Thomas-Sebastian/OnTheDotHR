package com.codeup.onthedothr.models;

import javax.persistence.*;

@Entity
@Table(name = "deliverableAttachments")
public class DeliverableAttachments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliverable_id")
    private Deliverable deliverable;

}
