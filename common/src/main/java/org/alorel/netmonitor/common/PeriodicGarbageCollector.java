/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Art on 16/12/2016.
 */
public class PeriodicGarbageCollector extends Thread {

    public static final AtomicLong interval = new AtomicLong(10000);

    private final static PeriodicGarbageCollector singleton = new PeriodicGarbageCollector();

    private PeriodicGarbageCollector() {
        setName("Periodic GC");
        setDaemon(true);
        setPriority(MIN_PRIORITY);
    }

    public static PeriodicGarbageCollector getInstance() {
        return singleton;
    }

    @Override
    public void start() {
        try {
            super.start();
            System.out.println("[Periodic GC] started");
        } catch (final IllegalThreadStateException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (true) {
            try {
                synchronized (this) {
                    wait(interval.get());
                }
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            System.runFinalization();
            System.gc();
            System.out.println("[Periodic GC] ticked");
        }
    }
}
