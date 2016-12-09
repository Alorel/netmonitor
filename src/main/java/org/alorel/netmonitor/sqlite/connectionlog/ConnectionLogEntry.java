/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.sqlite.connectionlog;

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
import java.util.Date;

/**
 * Connection event entry
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
@DatabaseTable(tableName = "connection_log")
@SchemaVersion(0.1)
public class ConnectionLogEntry extends AbstractEntity<ConnectionLogEntry> {

    /**
     * The entity's dao
     */
    private static final Dao<ConnectionLogEntry, Integer> dao;

    static {
        final ConnectionSource src = SQLiteFactory.getSource();
        dao = SQLiteFactory.createDao(src, ConnectionLogEntry.class, Integer.class);

        try {
            TableUtils.createTableIfNotExists(src, ConnectionLogEntry.class);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Dao<ConnectionLogEntry, ?> getDao() {
        return dao;
    }

    /**
     * The auto-incremented key
     */
    @DatabaseField(
            canBeNull = false,
            generatedId = true,
            columnName = "row_id"
    )
    private static Integer rowID;

    /**
     * Timestamp for when this event occurred
     */
    @DatabaseField(
            canBeNull = false,
            index = true,
            indexName = "date_state"
    )
    private static Date date;

    /**
     * Whether the link went up or down
     */
    @DatabaseField(
            canBeNull = false,
            index = true,
            indexName = "date_state"
    )
    private State state;

    /**
     * Constructor
     */
    public ConnectionLogEntry() {
        super();
    }

    /**
     * Constructor
     *
     * @param state The link state
     */
    private ConnectionLogEntry(final State state) {
        date = new Date();
        this.state = state;
    }

    /**
     * Log a connection becoming available
     */
    public static void logUp() {
        new ConnectionLogEntry(State.UP).save();
    }

    /**
     * Log a connection becoming unavailable
     */
    public static void logDown() {
        new ConnectionLogEntry(State.DOWN).save();
    }
}
