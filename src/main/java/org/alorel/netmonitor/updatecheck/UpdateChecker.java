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
 * Created by Art on 09/12/2016.
 */
public class UpdateChecker {

    private static final URL updateURL;

    static {
        try {
            updateURL = new URL("https://cdn.rawgit.com/Alorel/netmonitor/master/VERSION");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

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
