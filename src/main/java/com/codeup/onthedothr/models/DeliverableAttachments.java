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

    public DeliverableAttachments(){}

    public DeliverableAttachments(String fileUrl, Deliverable deliverable) {
        this.fileUrl = fileUrl;
        this.deliverable = deliverable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Deliverable getDeliverable() {
        return deliverable;
    }

    public void setDeliverable(Deliverable deliverable) {
        this.deliverable = deliverable;
    }
}
