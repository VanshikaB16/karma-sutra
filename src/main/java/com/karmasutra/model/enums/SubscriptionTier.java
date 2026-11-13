package com.karmasutra.model.enums;

public enum SubscriptionTier {
    FREE_DEVOTEE("Free Devotee", 0.0),
    KARMA_SUTRA_PRO("Karma Sutra PRO", 49.0),
    VIP_DEVOTEE("VIP Devotee", 99.0);

    private final String displayName;
    private final double monthlyPrice;

    SubscriptionTier(String displayName, double monthlyPrice) {
        this.displayName = displayName;
        this.monthlyPrice = monthlyPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }
}
