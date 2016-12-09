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
 * Created by Art on 09/12/2016.
 */
public class Monitor extends Thread {

    private final static ReadOnlyBooleanWrapper isUp = new ReadOnlyBooleanWrapper(false);

    private final static long INTERVAL = 5000;

    private final static int TIMEOUT = 3000;

    private static final String HOST = "www.google.com";

    private static volatile Date changeDate = new Date();

    private static final Lock lock = new ReentrantLock(true);

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

    private Monitor() {
        setName("Connectivity monitor");
        setDaemon(true);
        setPriority(Thread.MIN_PRIORITY);
    }

    public static void init() {
        singleton.start();
    }

    public static Date getChangeDate() {
        try {
            lock.lock();
            return changeDate;
        } finally {
            lock.unlock();
        }
    }

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

    public static ReadOnlyBooleanProperty getUptimeProperty() {
        return isUp.getReadOnlyProperty();
    }

}
