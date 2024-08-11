package com.omarkandil.exchange;

import java.util.prefs.Preferences;
// This is a class which manages the accessibility boolean. It also stores it in a preferences object for permanent storage
// until the user logs out. If the boolean enabled is true, it will indicate that the voice over should be called.

public class SettingsManager {
    private static final Preferences PREFS = Preferences.userRoot().node(SettingsManager.class.getName());
    private static final String ACCESSIBILITY_KEY = "accessibility";

    public static boolean isAccessibilityEnabled() {
        return PREFS.getBoolean(ACCESSIBILITY_KEY, false);
    }

    public static void setAccessibilityEnabled(boolean enabled) {
        PREFS.putBoolean(ACCESSIBILITY_KEY, enabled);
    }
}
