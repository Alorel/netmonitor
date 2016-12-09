/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Art on 09/12/2016.
 */
public abstract class AbstractEntity<T extends AbstractEntity> {

    protected abstract Dao<T, ?> getDao();

    @SuppressWarnings("unchecked")
    public void save() {
        try {
            getDao().createOrUpdate((T) this);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void saveIfNotExists() {
        try {
            getDao().createIfNotExists((T) this);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
