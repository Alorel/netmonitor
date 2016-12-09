/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.updatecheck;

import org.alorel.netmonitor.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Checks for new application versions
 *
 * @author a.molcanovas@gmail.com
 */
public class UpdateChecker {

    /**
     * The URL to check for updates
     */
    private static final URL updateURL;

    static {
        try {
            updateURL = new URL("https://cdn.rawgit.com/Alorel/netmonitor/master/VERSION");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check whether the application has a newer version available
     *
     * @return An UpdateStatus showing whether a new version is available
     */
    public static UpdateStatus hasUpdate() {
        try (final InputStream is = updateURL.openStream()) {
            final Version remoteVersion = new Version(IOUtil.read(is));

            return Version.CURRENT.compareTo(remoteVersion) > 0 ? UpdateStatus.NEW_VERSION_AVAILABLE : UpdateStatus.NO_UPDATES;
        } catch (final IOException e) {
            e.printStackTrace();
            return UpdateStatus.ERROR;
        }
    }
}
