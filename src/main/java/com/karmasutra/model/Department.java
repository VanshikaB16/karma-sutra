package com.karmasutra.model;

import java.sql.Timestamp;

public class Department {
    private int id;
    private String code;
    private String name;
    private String divineHead;
    private String description;
    private String escalationProtocol;
    private Timestamp createdAt;

    public Department() {
    }

    public Department(int id, String code, String name, String divineHead, String description, String escalationProtocol, Timestamp createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.divineHead = divineHead;
        this.description = description;
        this.escalationProtocol = escalationProtocol;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivineHead() {
        return divineHead;
    }

    public void setDivineHead(String divineHead) {
        this.divineHead = divineHead;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEscalationProtocol() {
        return escalationProtocol;
    }

    public void setEscalationProtocol(String escalationProtocol) {
        this.escalationProtocol = escalationProtocol;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }
}
