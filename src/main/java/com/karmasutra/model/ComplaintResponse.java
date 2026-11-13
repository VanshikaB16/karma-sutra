package com.karmasutra.model;

import java.sql.Timestamp;

public class ComplaintResponse {
    private int id;
    private int complaintId;
    private String responderType; // "AI_DIVINE_COUNCIL", "ARCH_DEITY_ADMIN"
    private String divinePersona;
    private String responseText;
    private String emotionalTone;
    private int iterationNumber;
    private Timestamp createdAt;

    public ComplaintResponse() {
        this.responderType = "AI_DIVINE_COUNCIL";
        this.iterationNumber = 1;
        this.emotionalTone = "NEUTRAL_BUREAUCRATIC";
    }

    public ComplaintResponse(int id, int complaintId, String responderType, String divinePersona,
                             String responseText, String emotionalTone, int iterationNumber, Timestamp createdAt) {
        this.id = id;
        this.complaintId = complaintId;
        this.responderType = responderType;
        this.divinePersona = divinePersona;
        this.responseText = responseText;
        this.emotionalTone = emotionalTone;
        this.iterationNumber = iterationNumber;
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

    public String getResponderType() {
        return responderType;
    }

    public void setResponderType(String responderType) {
        this.responderType = responderType;
    }

    public String getDivinePersona() {
        return divinePersona;
    }

    public void setDivinePersona(String divinePersona) {
        this.divinePersona = divinePersona;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getEmotionalTone() {
        return emotionalTone;
    }

    public void setEmotionalTone(String emotionalTone) {
        this.emotionalTone = emotionalTone;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
