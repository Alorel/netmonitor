/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.awt;

import java.awt.MenuItem;

/**
 * An extension to the standard MenuItems - this one is controlled by a boolean on/off switch
 *
 * @author a.molcanovas@gmail.com
 */
public class BooleanMenuItem extends MenuItem {

    /**
     * The format for {@link String#format(String, Object...)}
     */
    private final String format;

    /**
     * Whether the item is currently enabled
     */
    private volatile boolean isCurrentlyEnabled;

    /**
     * Constructor
     *
     * @param isCurrentlyEnabled Whether the item is currently enabled
     * @param baseLabel          The base label, e.g. the name of the setting
     */
    public BooleanMenuItem(final boolean isCurrentlyEnabled, final String baseLabel) {
        super();
        format = String.format("%s %s", "%s", baseLabel);
        setCurrentlyEnabled(isCurrentlyEnabled);
    }

    /**
     * Check whether the item is currently enabled
     *
     * @return {@link #isCurrentlyEnabled}
     */
    public synchronized boolean isCurrentlyEnabled() {
        return isCurrentlyEnabled;
    }

    /**
     * If the item is currently enabled, disable it. If it is currently disabled, enable it.
     *
     * @return The new value of {@link #isCurrentlyEnabled}
     */
    public synchronized boolean flip() {
        setCurrentlyEnabled(!isCurrentlyEnabled);
        return isCurrentlyEnabled;
    }

    /**
     * Set whether the item is currently enabled
     *
     * @param currentlyEnabled The switch
     */
    public synchronized void setCurrentlyEnabled(final boolean currentlyEnabled) {
        isCurrentlyEnabled = currentlyEnabled;
        setLabel(String.format(format, currentlyEnabled ? "Disable" : "Enable"));
    }
}
