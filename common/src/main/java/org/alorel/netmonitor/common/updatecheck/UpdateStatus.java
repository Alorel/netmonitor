/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common.updatecheck;

/**
 * Status on a new application version
 *
 * @author a.molcanovas@gmail.com
 */
public enum UpdateStatus {
    /**
     * A new version is available
     */
    NEW_VERSION_AVAILABLE,

    /**
     * No updates available
     */
    NO_UPDATES,

    /**
     * An error occurred
     */
    ERROR
}
