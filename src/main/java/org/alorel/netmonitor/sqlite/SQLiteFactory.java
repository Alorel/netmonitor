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
 * SQLite connection factory
 *
 * @author a.molcanovas@gmail.com
 */
public class SQLiteFactory {

    /**
     * The connection String required by JDBC
     */
    private static final String CONNECTION_STRING = "jdbc:sqlite:netmonitor.db";

    static {
        try {
            Class.forName(JDBC.class.getName());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the ORM Connection source
     *
     * @return A new connection source to be used with the ORM library
     * @throws RuntimeException As a wrapper for any other exception
     */
    public static ConnectionSource getSource() {
        try {
            return new JdbcConnectionSource(CONNECTION_STRING);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the JDBC connection
     *
     * @return A new JDBC connection
     * @throws RuntimeException As a wrapper for any other exception
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(CONNECTION_STRING);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper function to create a Dao. Uses {@link DaoManager#createDao(ConnectionSource, Class)}.
     *
     * @param src      The connection source to create the Dao from
     * @param entity   The entity class that manages this Dao
     * @param keyClass Type of the primary key for this entity
     * @param <C>      The entity class
     * @param <K>      The primary key type
     * @return The created Dao.
     */
    public static <C, K> Dao<C, K> createDao(final ConnectionSource src, final Class<C> entity, final Class<K> keyClass) {
        try {
            return DaoManager.createDao(src, entity);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
