package com.karmasutra.model;

import java.sql.Date;
import java.sql.Timestamp;

public class WishMilestone {
    private int id;
    private int complaintId;
    private String title;
    private Date targetDate;
    private String status; // "PENDING", "ACHIEVED", "DEFERRED"
    private String cosmicNote;
    private Timestamp createdAt;

    public WishMilestone() {
        this.status = "PENDING";
    }

    public WishMilestone(int id, int complaintId, String title, Date targetDate, String status, String cosmicNote, Timestamp createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.title = title;
        this.targetDate = targetDate;
        this.status = status;
        this.cosmicNote = cosmicNote;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(int complaintId) {
        this.complaintId = complaintId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCosmicNote() {
        return cosmicNote;
    }

    public void setCosmicNote(String cosmicNote) {
        this.cosmicNote = cosmicNote;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
