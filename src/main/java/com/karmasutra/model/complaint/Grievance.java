package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintType;

/**
 * Grievance represents an explicit protest against cosmic injustice,
 * bureaucratic delay, peer luck imbalance, or existential friction.
 */
public class Grievance extends Complaint {

    private String offendingParty; // e.g. "Colleague", "Professor", "Relative", "Fate"
    private boolean requiresApologyRitual;

    public Grievance() {
        super();
        setType(ComplaintType.COMPLAINT);
    }

    @Override
    public String getBureaucraticClassification() {
        return "GRIEVANCE-FORM-88B [Injustice Audit]";
    }

    @Override
    public int calculateKarmaWeight() {
        // Grievances carry high cosmic penalty scrutiny
        return 15 * getSeverity().getLevel();
    }

    @Override
    public String getSuggestedEscalationLevel() {
        return switch (getSeverity()) {
            case COSMIC_CRITICAL -> "Immediate Subpoena to the Supreme Department of Retributive Karma";
            case HIGH -> "Forward to Departmental Lead for Formal Audit";
            default -> "Standard Docket Queue (Routine Grievance Desk)";
        };
    }

    public String getOffendingParty() {
        return offendingParty;
    }

    public void setOffendingParty(String offendingParty) {
        this.offendingParty = offendingParty;
    }

    public boolean isRequiresApologyRitual() {
        return requiresApologyRitual;
    }

    public void setRequiresApologyRitual(boolean requiresApologyRitual) {
        this.requiresApologyRitual = requiresApologyRitual;
    }
}
