package com.karmasutra.model.enums;

public enum UserRole {
    DEVOTEE("Devotee"),
    ARCH_DEITY_ADMIN("Arch-Deity Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
