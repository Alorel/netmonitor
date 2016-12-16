/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.alorel.netmonitor.common.collections.CyclicIterable;
import org.alorel.netmonitor.common.sqlite.config.Config;
import org.alorel.netmonitor.common.sqlite.config.Keys;
import org.alorel.netmonitor.common.sqlite.connectionlog.ConnectionLogEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Connection state monitor
 *
 * @author a.molcanovas@gmail.com
 */
public class Monitor extends Thread {

    /**
     * A boolean property showing whether a connection is available
     */
    private final static ReadOnlyBooleanWrapper isUp = new ReadOnlyBooleanWrapper(false);

    /**
     * Date of the last connection state change
     */
    private static volatile Date changeDate = new Date();

    /**
     * A lock
     */
    private static final Lock lock = new ReentrantLock(true);

    /**
     * This class' singleton instance
     */
    private static final Monitor singleton;

    static {
        PeriodicGarbageCollector.getInstance().start();
        isUp.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                Tray.setUp();
            } else {
                Tray.setDown();
            }

            if (Config.getBoolean(Keys.LOG_CONNECT_STATUS)) {
                if (newValue) {
                    ConnectionLogEntry.logUp();
                } else {
                    ConnectionLogEntry.logDown();
                }
            }
        });

        singleton = new Monitor();
    }

    private long waitTime;
    private int connectionTimeout;
    private int maxFailures;

    private Stream<URL> hosts;

    /**
     * Constructor
     */
    private Monitor() {
        setName("Connectivity monitor");
        setDaemon(true);
        setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Start the singleton instance
     */
    public static void init() {
        singleton.start();
    }

    /**
     * Get the date when the last state change occurred
     *
     * @return {@link #changeDate}
     */
    public static Date getChangeDate() {
        try {
            lock.lock();
            return changeDate;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Set whether a connection is current possible
     *
     * @param connectionIsUp True if it is, false if it isn't
     */
    private static void setConnectionIsUp(final boolean connectionIsUp) {
        try {
            lock.lock();
            changeDate = new Date();
        } finally {
            lock.unlock();
            isUp.set(connectionIsUp);
        }
    }

    private void readConfig() throws IOException {
        System.out.println("Reading configuration");

        final Properties p = new Properties();
        try (final InputStream is = Monitor.class.getResourceAsStream("/org/alorel/netmonitor/config.properties")) {
            p.load(is);
        }

        waitTime = Long.parseUnsignedLong(p.getProperty("check_interval"));
        connectionTimeout = Integer.parseUnsignedInt(p.getProperty("check_timeout"));
        maxFailures = Integer.parseUnsignedInt(p.getProperty("successive_timeouts_to_mark_connection_down"));

        System.out.println("Check interval set to " + waitTime);
        System.out.println("Connection timeout set to " + connectionTimeout);
        System.out.println("Successive timeout limit set to " + maxFailures);

        final String[] hosts = p.getProperty("hosts").split(",");
        final String format = "http://%s";

        System.out.println("Transforming URLs");
        final List<URL> urlList = Arrays.stream(hosts)
                .map(h -> String.format(format, h))
                .map(h -> {
                    try {
                        return new URL(h);
                    } catch (final MalformedURLException e) {
                        throw new RuntimeException(e.getLocalizedMessage(), e);
                    }
                }).collect(Collectors.toList());

        System.out.println("Creating cyclic URL stream");
        this.hosts = new CyclicIterable<>(urlList.toArray(new URL[urlList.size()])).stream();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        try {
            readConfig();
        } catch (final IOException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }

        final String reachableFormat = "%s contacted in %d ms%n";
        final String unreachableFormat = "%s unreachable: %s%n";
        final String successiveFormat = "%d successive connection attempts failed. Marking connection as down.%n";
        final String requestMethod = "HEAD";
        final AtomicInteger successives = new AtomicInteger(0);

        hosts.forEach(host -> {
            final long start = System.currentTimeMillis();
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) host.openConnection();
                conn.setRequestMethod(requestMethod);
                conn.setConnectTimeout(connectionTimeout);

                try {
                    conn.connect();
                    System.out.printf(reachableFormat, host, System.currentTimeMillis() - start);
                    setConnectionIsUp(true);
                    successives.set(0);
                } catch (final IOException e) {
                    System.err.printf(unreachableFormat, host, e.getLocalizedMessage());
                    final int successiveFailures;

                    if ((successiveFailures = successives.incrementAndGet()) >= maxFailures) {
                        setConnectionIsUp(false);
                        System.err.printf(successiveFormat, successiveFailures);
                    }
                }

            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
                try {
                    synchronized (Monitor.this) {
                        Monitor.this.wait(waitTime);
                    }
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Return the uptime property so additional event listeners can be added
     *
     * @return {@link #isUp} as a Read-Only property
     */

    public static ReadOnlyBooleanProperty getUptimeProperty() {
        return isUp.getReadOnlyProperty();
    }

}
