/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite;

/**
 * The database schema version
 *
 * @author a.molcanovas@gmail.com
 */
public @interface SchemaVersion {

    /**
     * The current version of the entity's schema
     *
     * @return The current version of the entity's schema
     */
    double value();
}
