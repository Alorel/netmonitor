/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common.sqlite.config;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import org.alorel.netmonitor.common.sqlite.AbstractEntity;
import org.alorel.netmonitor.common.sqlite.SQLiteFactory;
import org.alorel.netmonitor.common.sqlite.SchemaVersion;

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

    /**
     * This entity's Dao
     */
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
        new Config(Keys.SOUND_ENABLED, true).saveIfNotExists();
    }

    /**
     * Config item key
     */
    @DatabaseField(
            id = true,
            canBeNull = false,
            columnDefinition = "VARCHAR NOT NULL COLLATE NOCASE"
    )
    private String key;

    /**
     * Config item value
     */
    @DatabaseField(
            canBeNull = false,
            columnDefinition = "VARCHAR COLLATE NOCASE"
    )
    private String value;

    /**
     * Constructor
     */
    public Config() {
        super();
    }

    /**
     * Constructor
     *
     * @param key   The config item's key
     * @param value The config item's value
     */
    private Config(final String key, final String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Constructor
     *
     * @param key   The config item's key
     * @param value The config item's value
     */
    private Config(final String key, final boolean value) {
        this(key, value ? "1" : "0");
    }

    @Override
    protected Dao<Config, ?> getDao() {
        return dao;
    }

    /**
     * Set a configuration entry
     *
     * @param key   Configuration item key
     * @param value Configuration item value
     */
    public static void set(final String key, final String value) {
        new Config(key, value).save();
    }

    /**
     * Set a configuration entry
     *
     * @param key   Configuration item key
     * @param value Configuration item value
     */
    public static void set(final String key, final boolean value) {
        new Config(key, value ? "1" : "0").save();
    }

    /**
     * Get a config item
     *
     * @param key Item key
     * @return An optional for this configuration key
     */
    private static Optional<Config> get(final String key) {
        try {
            return Optional.ofNullable(dao.queryForId(key));
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a configuration item
     *
     * @param key Configuration item key
     * @return The configuration item as a string
     */
    public static String getString(final String key) {
        final Optional<Config> o = get(key);
        return o.isPresent() ? o.get().value : null;
    }

    /**
     * Get a configuration item
     *
     * @param key          Configuration item key
     * @param defaultValue The default value if the key does not exist
     * @return The configuration item as a boolean
     */
    public static boolean getBoolean(final String key, final boolean defaultValue) {
        final Optional<Config> o = get(key);
        return o.isPresent() ? "1".equals(o.get().value) : defaultValue;
    }

    /**
     * Get a configuration item
     *
     * @param key Configuration item key
     * @return The configuration item as a boolean
     */
    public static boolean getBoolean(final String key) {
        return getBoolean(key, false);
    }
}
