package com.karmasutra.model.enums;

public enum ComplaintType {
    COMPLAINT("Complaint"),
    PRAYER("Prayer"),
    WISH("Wish"),
    PERSONAL_DILEMMA("Personal Dilemma");

    private final String label;

    ComplaintType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ComplaintType fromLabel(String label) {
        for (ComplaintType type : values()) {
            if (type.label.equalsIgnoreCase(label) || type.name().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return COMPLAINT;
    }
}
