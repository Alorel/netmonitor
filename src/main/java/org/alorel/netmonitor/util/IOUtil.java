/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * I/O utilities
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class IOUtil {

    /**
     * Read an input stream
     *
     * @param is The input stream
     * @return The contents
     * @throws RuntimeException as a wrapper for any IOException
     */
    public static String read(final InputStream is) {
        try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            return read(isr);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read from an InputStreamReader
     *
     * @param isr The input stream reader
     * @return The contents
     * @throws RuntimeException as a wrapper for any IOException
     */
    public static String read(final InputStreamReader isr) {
        try (final BufferedReader br = new BufferedReader(isr)) {
            return read(br);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read from a BufferedReader
     *
     * @param br The buffered reader
     * @return The contents
     * @throws RuntimeException as a wrapper for any IOException
     */
    public static String read(final BufferedReader br) {
        try {
            String line = br.readLine();

            if (null != line) {
                final StringBuilder sb = new StringBuilder(line);

                while ((line = br.readLine()) != null) {
                    sb.append('\n').append(line);
                }

                return sb.toString();
            }

            return "";
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read from a bundled file
     *
     * @param path Path to the bundled file
     * @return The contents
     * @throws RuntimeException as a wrapper for any IOException
     */
    public static String readBundledFile(final String path) {
        try (final InputStream is = IOUtil.class.getResourceAsStream(path)) {
            return read(is);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
