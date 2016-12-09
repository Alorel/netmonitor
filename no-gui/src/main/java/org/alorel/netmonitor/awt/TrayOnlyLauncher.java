/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.awt;

import org.alorel.netmonitor.common.Monitor;
import org.alorel.netmonitor.common.Tray;
import org.alorel.netmonitor.common.sqlite.SQLiteFactory;
import org.alorel.netmonitor.common.sqlite.config.Config;
import org.alorel.netmonitor.common.sqlite.config.Keys;
import org.alorel.netmonitor.common.sqlite.connectionlog.ConnectionLogEntry;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Tray-Only version of the app
 *
 * @author a.molcanovas@gmail.com
 */
public class TrayOnlyLauncher {

    /**
     * Shortcut to the PopupMenu
     */
    private static PopupMenu popupMenu;

    /**
     * Vacuum SQLite
     *
     * @throws SQLException If one occurs
     */
    private static void vacuum() throws SQLException {
        // Compact the database
        try (final Connection conn = SQLiteFactory.getConnection()) {
            try (final Statement st = conn.createStatement()) {
                st.execute("VACUUM");
            }
        }
    }

    /**
     * Initialise classes with blocking static constructors
     *
     * @throws ClassNotFoundException Never
     */
    private static void initClasses() throws ClassNotFoundException {
        Class.forName(ConnectionLogEntry.class.getName());
        Class.forName(Monitor.class.getName());
    }

    /**
     * Init the setting menu for {@link Keys#SOUND_ENABLED}
     */
    private static void initSFXSetting() {
        final BooleanMenuItem sfxMenu = new BooleanMenuItem(
                Config.getBoolean(Keys.SOUND_ENABLED),
                "sound notification on connection state change"
        );

        sfxMenu.addActionListener(e -> Config.set(Keys.SOUND_ENABLED, sfxMenu.flip()));
        popupMenu.add(sfxMenu);
    }

    /**
     * Init the setting menu for {@link Keys#LOG_CONNECT_STATUS}
     */
    private static void initLoggingSetting() {
        final BooleanMenuItem sfxMenu = new BooleanMenuItem(
                Config.getBoolean(Keys.LOG_CONNECT_STATUS),
                "connection state change timestamp logging"
        );

        sfxMenu.addActionListener(e -> Config.set(Keys.SOUND_ENABLED, sfxMenu.flip()));
        popupMenu.add(sfxMenu);
    }

    /**
     * Init the app
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        if (!SystemTray.isSupported()) {
            System.err.println("Your system does not support the SystemTray; the application cannot function.");
            System.exit(1);
        } else try {
            //Prepare tray icon
            Tray.init();
            popupMenu = Tray.getTrayIcon().getPopupMenu();
            popupMenu.addSeparator();

            vacuum();
            initClasses();

            initSFXSetting();
            initLoggingSetting();

            Monitor.init();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
