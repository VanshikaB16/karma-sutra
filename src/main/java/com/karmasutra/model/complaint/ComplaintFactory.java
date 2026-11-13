package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintType;

/**
 * Factory for polymorphic Complaint instantiation.
 */
public final class ComplaintFactory {

    private ComplaintFactory() {
    }

    public static Complaint createComplaint(ComplaintType type) {
        if (type == null) {
            return new Grievance();
        }
        return switch (type) {
            case PRAYER -> new Prayer();
            case WISH -> new Wish();
            case PERSONAL_DILEMMA -> new PersonalDilemma();
            case COMPLAINT -> new Grievance();
        };
    }
}
