package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintType;

/**
 * Prayer represents a reverent petition for divine intervention, peace,
 * protection, exam blessing, or spiritual reassurance.
 */
public class Prayer extends Complaint {

    private String devotionIntensity;
    private boolean fastObserved;

    public Prayer() {
        super();
        setType(ComplaintType.PRAYER);
    }

    @Override
    public String getBureaucraticClassification() {
        return "DEVOTIONAL-PETITION-108 [Sanctuary Queue]";
    }

    @Override
    public int calculateKarmaWeight() {
        // Prayers generate positive karmic goodwill
        return 20 + (5 * getSeverity().getLevel());
    }

    @Override
    public String getSuggestedEscalationLevel() {
        return switch (getSeverity()) {
            case COSMIC_CRITICAL -> "Emergency Cosmic Sanctuary Expedited Blessing";
            case HIGH -> "Assigned to Senior Emissary for Compassionate Review";
            default -> "Universal Morning Blessing Broadcast Docket";
        };
    }

    public String getDevotionIntensity() {
        return devotionIntensity;
    }

    public void setDevotionIntensity(String devotionIntensity) {
        this.devotionIntensity = devotionIntensity;
    }

    public boolean isFastObserved() {
        return fastObserved;
    }

    public void setFastObserved(boolean fastObserved) {
        this.fastObserved = fastObserved;
    }
}
