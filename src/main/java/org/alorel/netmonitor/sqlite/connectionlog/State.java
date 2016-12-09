/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite.connectionlog;

/**
 * Connection states
 *
 * @author a.molcanovas@gmail.com
 */
public enum State {
    /**
     * A connection is available
     */
    UP,
    /**
     * A connection is unavailable
     */
    DOWN
}
