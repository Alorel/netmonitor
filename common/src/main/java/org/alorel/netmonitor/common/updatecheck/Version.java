/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common.updatecheck;

import org.alorel.netmonitor.common.util.IOUtil;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Used to compare version
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class Version implements Comparable<Version> {

    private final String version;

    public static final Version CURRENT = new Version(IOUtil.readBundledFile("/VERSION"));

    public Version(final String version) {
        this.version = Objects.requireNonNull(version);
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        Version version1 = (Version) o;
        return Objects.equals(getVersion(), version1.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion());
    }

    @Override
    public int compareTo(@Nullable final Version that) {
        if (that == null) {
            return 1;
        }

        final String[] thisParts = getVersion().split("\\.");
        final String[] thatParts = that.getVersion().split("\\.");
        final int length = Math.max(thisParts.length, thatParts.length);

        for (int i = 0; i < length; i++) {
            final int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            final int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;

            if (thisPart < thatPart) {
                return -1;
            }

            if (thisPart > thatPart) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return getVersion();
    }
}
