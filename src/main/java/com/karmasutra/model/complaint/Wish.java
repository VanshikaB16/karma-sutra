package com.karmasutra.model.complaint;

import com.karmasutra.model.enums.ComplaintType;

/**
 * Wish represents a material, aspirational, or career milestone request.
 * Supports long-term destiny classification and Karma Sutra PRO™ escalation.
 */
public class Wish extends Complaint {

    private int horizonYears;
    private String intendedMilestoneSummary;

    public Wish() {
        super();
        setType(ComplaintType.WISH);
    }

    @Override
    public String getBureaucraticClassification() {
        if (isLongTermWish()) {
            return "ASPIRATIONAL-ROADMAP-2035 [Multi-Decade Destiny Project]";
        }
        return "IMMEDIATE-WISH-VOUCHER [Temporal Fulfillment Office]";
    }

    @Override
    public int calculateKarmaWeight() {
        // High aspirations require sustained karmic deposit
        return isLongTermWish() ? 50 : 10 * getSeverity().getLevel();
    }

    @Override
    public String getSuggestedEscalationLevel() {
        if (isLongTermWish()) {
            return "Eligible for Karma Sutra PRO™ Multi-Year Destiny Tracking";
        }
        return "Standard Materialization Pipeline";
    }

    public int getHorizonYears() {
        return horizonYears;
    }

    public void setHorizonYears(int horizonYears) {
        this.horizonYears = horizonYears;
    }

    public String getIntendedMilestoneSummary() {
        return intendedMilestoneSummary;
    }

    public void setIntendedMilestoneSummary(String intendedMilestoneSummary) {
        this.intendedMilestoneSummary = intendedMilestoneSummary;
    }
}
