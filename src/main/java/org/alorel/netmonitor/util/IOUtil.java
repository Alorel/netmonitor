/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Art on 09/12/2016.
 */
@ParametersAreNonnullByDefault
public class IOUtil {

    public static String readBundledFile(final String path) {
        try (final InputStream is = IOUtil.class.getResourceAsStream(path)) {
            try (final InputStreamReader isr = new InputStreamReader(is)) {
                try (final BufferedReader br = new BufferedReader(isr)) {
                    String line = br.readLine();

                    if (null != line) {
                        final StringBuilder sb = new StringBuilder(line);

                        while ((line = br.readLine()) != null) {
                            sb.append('\n').append(line);
                        }

                        return sb.toString();
                    }
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return "";
    }
}
