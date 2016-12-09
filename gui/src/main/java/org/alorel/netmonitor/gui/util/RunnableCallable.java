/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui.util;

import java.util.concurrent.Callable;

/**
 * An abstract class where the developer needs to implement the {@link #call()} method,
 * which will simply be run if the class is used as a {@link Runnable}. This is just a more lightweight version of
 * {@link javafx.concurrent.Task JavaFX's Task class}
 *
 * @author a.molcanovas@gmail.com
 */
public interface RunnableCallable<T> extends Runnable, Callable<T> {

    /**
     * Returns some value, computing it if necessary. This differs from {@link Callable#call()} as it cannot throw an
     * exception
     *
     * @return The value
     */
    @Override
    T call();

    @Override
    default void run() {
        call();
    }
}