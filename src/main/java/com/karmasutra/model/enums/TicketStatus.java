package com.karmasutra.model.enums;

public enum TicketStatus {
    RECEIVED_BY_HEAVEN("Received by Heaven"),
    UNDER_DIVINE_REVIEW("Under Divine Review"),
    FORWARDED_TO_KARMA_DEPT("Forwarded to Karma Department"),
    AWAITING_COSMIC_APPROVAL("Awaiting Cosmic Approval"),
    RESOLVED("Resolved"),
    ON_HOLD_BANDWIDTH("On Hold: Divine Bandwidth Issues");

    private final String label;

    TicketStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TicketStatus fromLabel(String label) {
        for (TicketStatus status : values()) {
            if (status.label.equalsIgnoreCase(label) || status.name().equalsIgnoreCase(label)) {
                return status;
            }
        }
        return RECEIVED_BY_HEAVEN;
    }
}
