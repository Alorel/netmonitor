/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Art on 09/12/2016.
 */
public class SQLiteFactory {
    private static final String CONNECTION_STRING = "jdbc:sqlite:netmonitor.db";

    static {
        try {
            Class.forName(JDBC.class.getName());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }

    public static ConnectionSource getSource() {
        try {
            return new JdbcConnectionSource(CONNECTION_STRING);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(CONNECTION_STRING);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <C, K> Dao<C, K> createDao(final Class<C> entity, final Class<K> keyClass) {
        return createDao(getSource(), entity, keyClass);
    }

    public static <C, K> Dao<C, K> createDao(final ConnectionSource src, final Class<C> entity, final Class<K> keyClass) {
        try {
            return DaoManager.createDao(src, entity);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
