/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;
import org.alorel.netmonitor.sqlite.connectionlog.ConnectionLogEntry;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
     * Pause between subsequent checks
     */
    private final static long INTERVAL = 5000;

    /**
     * Connection timeout
     */
    private final static int TIMEOUT = 3000;

    /**
     * Host to use for checking
     */
    private static final String HOST = "www.google.com";

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

    /**
     * Constructor
     */
    private Monitor() {
        setName("Connectivity monitor");
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
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
            isUp.set(connectionIsUp);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            boolean hasBeenSet = false;

            try {
                for (final InetAddress address : InetAddress.getAllByName(HOST)) {
                    if (address.isReachable(TIMEOUT)) {
                        setConnectionIsUp(true);
                        hasBeenSet = true;
                        break;
                    } else {
                        System.out.printf("%s could not be contacted%n", address);
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (!hasBeenSet) {
                setConnectionIsUp(false);
            }

            try {
                synchronized (this) {
                    wait(INTERVAL);
                }
            } catch (final InterruptedException e) {
                System.out.printf("Interrupted:%n%s%n", e.getLocalizedMessage());
            }
        }
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
