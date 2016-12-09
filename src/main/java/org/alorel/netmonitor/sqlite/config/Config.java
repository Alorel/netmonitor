/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.alorel.netmonitor.sqlite.AbstractEntity;
import org.alorel.netmonitor.sqlite.SQLiteFactory;
import org.alorel.netmonitor.sqlite.SchemaVersion;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Configuration entry entity
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
@DatabaseTable(tableName = "config")
@SchemaVersion(0.1)
public class Config extends AbstractEntity<Config> {

    private static final Dao<Config, String> dao;

    static {
        final ConnectionSource src = SQLiteFactory.getSource();
        dao = SQLiteFactory.createDao(src, Config.class, String.class);

        try {
            TableUtils.createTableIfNotExists(src, Config.class);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        new Config(Keys.LOG_CONNECT_STATUS, false).saveIfNotExists();
        new Config(Keys.START_MINIMISED, false).saveIfNotExists();
    }

    @DatabaseField(
            id = true,
            canBeNull = false,
            columnDefinition = "VARCHAR NOT NULL COLLATE NOCASE"
    )
    private String key;

    @DatabaseField(
            canBeNull = false,
            columnDefinition = "VARCHAR COLLATE NOCASE"
    )
    private String value;

    public Config() {
        super();
    }

    private Config(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    private Config(final String key, final boolean value) {
        this(key, value ? "1" : "0");
    }

    @Override
    protected Dao<Config, ?> getDao() {
        return dao;
    }

    public static void set(final String key, final String value) {
        new Config(key, value).save();
    }

    public static void set(final String key, final boolean value) {
        new Config(key, value ? "1" : "0").save();
    }

    private static Optional<Config> get(final String key) {
        try {
            return Optional.ofNullable(dao.queryForId(key));
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(final String key) {
        final Optional<Config> o = get(key);
        return o.isPresent() ? o.get().value : null;
    }

    public static boolean getBoolean(final String key, final boolean defaultValue) {
        final Optional<Config> o = get(key);
        return o.isPresent() ? "1".equals(o.get().value) : defaultValue;
    }

    public static boolean getBoolean(final String key) {
        return getBoolean(key, false);
    }
}
