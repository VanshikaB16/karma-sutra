package com.karmasutra.model.enums;

public enum ComplaintCategory {
    ACADEMIC_SUFFERING("Academic Suffering", "DEPT_ACAD"),
    CAREER_AND_UNEMPLOYMENT("Career & Unemployment", "DEPT_CAREER"),
    LOVE_AND_RELATIONSHIPS("Love & Relationships", "DEPT_LOVE"),
    FAMILY_DRAMA("Family Drama", "DEPT_FAMILY"),
    FINANCIAL_DIFFICULTIES("Financial Difficulties", "DEPT_FIN"),
    KARMA_DEPARTMENT("Karma Department", "DEPT_KARMA"),
    DESTINY_AND_LIFE_DECISIONS("Destiny & Life Decisions", "DEPT_DESTINY"),
    OTHER_DIVINE_MATTERS("Other Divine Matters", "DEPT_MISC");

    private final String label;
    private final String defaultDeptCode;

    ComplaintCategory(String label, String defaultDeptCode) {
        this.label = label;
        this.defaultDeptCode = defaultDeptCode;
    }

    public String getLabel() {
        return label;
    }

    public String getDefaultDeptCode() {
        return defaultDeptCode;
    }

    public static ComplaintCategory fromLabel(String label) {
        for (ComplaintCategory cat : values()) {
            if (cat.label.equalsIgnoreCase(label) || cat.name().equalsIgnoreCase(label)) {
                return cat;
            }
        }
        return OTHER_DIVINE_MATTERS;
    }
}
