/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common.collections;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Art on 16/12/2016.
 */
public class CyclicIterable<T> implements Iterable<T> {

    private final T[] items;

    public CyclicIterable(final T[] items) {
        this.items = items;
    }

    @Override
    public Iterator<T> iterator() {
        return new CyclicIterator();
    }

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @ThreadSafe
    private class CyclicIterator implements Iterator<T> {

        private volatile int i = 0;

        public CyclicIterator() {
            System.out.println("Constructed");
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public synchronized T next() {
            i = Math.abs(++i);
            return items[i % items.length];
        }
    }
}
