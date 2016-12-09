/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite;

import com.j256.ormlite.dao.Dao;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SQLite entity superclass
 *
 * @author a.molcanovas@gmail.com
 */
public abstract class AbstractEntity<T extends AbstractEntity> {

    /**
     * A lock ensuring that no concurrent writes are being made
     */
    private static final Lock lock = new ReentrantLock(true);

    /**
     * Get the Dao for this entity
     *
     * @return The Dao for this entity
     */
    protected abstract Dao<T, ?> getDao();

    /**
     * Save the entity to SQLite. This performs a {@link Dao#createOrUpdate(Object)} operation.
     *
     * @throws RuntimeException As a wrapper for any other exception
     */
    @SuppressWarnings("unchecked")
    @OverridingMethodsMustInvokeSuper
    public void save() {
        try {
            lock.lock();
            getDao().createOrUpdate((T) this);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Saves the entity if it doesn't already exist in SQLite. This performs a {@link Dao#createIfNotExists(Object)}
     * operation
     *
     * @throws RuntimeException As a wrapper for any other exception
     */
    @SuppressWarnings("unchecked")
    public void saveIfNotExists() {
        try {
            lock.lock();
            getDao().createIfNotExists((T) this);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
