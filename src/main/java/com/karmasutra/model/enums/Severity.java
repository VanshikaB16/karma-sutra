package com.karmasutra.model.enums;

public enum Severity {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    COSMIC_CRITICAL("Cosmic Critical", 4);

    private final String label;
    private final int level;

    Severity(String label, int level) {
        this.label = label;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public int getLevel() {
        return level;
    }

    public static Severity fromLabel(String label) {
        for (Severity s : values()) {
            if (s.label.equalsIgnoreCase(label) || s.name().equalsIgnoreCase(label)) {
                return s;
            }
        }
        return MEDIUM;
    }
}
