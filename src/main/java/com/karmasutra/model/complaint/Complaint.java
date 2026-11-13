package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;

import java.sql.Timestamp;

/**
 * Abstract base class representing a divine petition or grievance.
 * Demonstrates OOP Abstraction, Encapsulation, and Polymorphism.
 */
public abstract class Complaint {

    private int id;
    private String ticketNumber;
    private int userId;
    private String subject;
    private String description;
    private ComplaintCategory category;
    private ComplaintType type;
    private Severity severity;
    private int departmentId;
    private TicketStatus status;
    private String desiredResolution;
    private boolean isLongTermWish;
    private boolean isEscalated;
    private int karmaPointsAwarded;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Associated metadata for UI display
    private String departmentName;
    private String submitterName;

    public Complaint() {
        this.status = TicketStatus.RECEIVED_BY_HEAVEN;
        this.severity = Severity.MEDIUM;
        this.isLongTermWish = false;
        this.isEscalated = false;
        this.karmaPointsAwarded = 0;
    }

    public Complaint(int id, String ticketNumber, int userId, String subject, String description,
                     ComplaintCategory category, ComplaintType type, Severity severity, int departmentId,
                     TicketStatus status, String desiredResolution, boolean isLongTermWish,
                     boolean isEscalated, int karmaPointsAwarded, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.userId = userId;
        this.subject = subject;
        this.description = description;
        this.category = category;
        this.type = type;
        this.severity = severity;
        this.departmentId = departmentId;
        this.status = status != null ? status : TicketStatus.RECEIVED_BY_HEAVEN;
        this.desiredResolution = desiredResolution;
        this.isLongTermWish = isLongTermWish;
        this.isEscalated = isEscalated;
        this.karmaPointsAwarded = karmaPointsAwarded;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==========================================
    // POLYMORPHIC ABSTRACT METHODS (Phase 9 OOP)
    // ==========================================

    /**
     * Specialized bureaucratic classification code based on complaint type behavior.
     */
    public abstract String getBureaucraticClassification();

    /**
     * Calculates the karma stake or points weight for cosmic balance calculation.
     */
    public abstract int calculateKarmaWeight();

    /**
     * Suggests institutional escalation path according to specialized cosmic protocol.
     */
    public abstract String getSuggestedEscalationLevel();

    // ==========================================
    // GETTERS & SETTERS (Strict Encapsulation)
    // ==========================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket subject cannot be empty");
        }
        this.subject = subject.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket description cannot be empty");
        }
        this.description = description.trim();
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public ComplaintType getType() {
        return type;
    }

    public void setType(ComplaintType type) {
        this.type = type;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getDesiredResolution() {
        return desiredResolution;
    }

    public void setDesiredResolution(String desiredResolution) {
        this.desiredResolution = desiredResolution;
    }

    public boolean isLongTermWish() {
        return isLongTermWish;
    }

    public void setLongTermWish(boolean longTermWish) {
        isLongTermWish = longTermWish;
    }

    public boolean isEscalated() {
        return isEscalated;
    }

    public void setEscalated(boolean escalated) {
        isEscalated = escalated;
    }

    public int getKarmaPointsAwarded() {
        return karmaPointsAwarded;
    }

    public void setKarmaPointsAwarded(int karmaPointsAwarded) {
        this.karmaPointsAwarded = karmaPointsAwarded;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }
}
