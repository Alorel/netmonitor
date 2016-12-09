/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.alorel.netmonitor.Monitor;
import org.alorel.netmonitor.Tray;
import org.alorel.netmonitor.sqlite.SQLiteFactory;
import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;
import org.alorel.netmonitor.sqlite.connectionlog.ConnectionLogEntry;
import org.alorel.netmonitor.util.FXDialog;
import org.alorel.netmonitor.util.FXUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by Art on 09/12/2016.
 */
@ParametersAreNonnullByDefault
public class GUIApp extends Application {

    private FXMLLoader loader;

    private Scene homeScene;

    private boolean showStage;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        Tray.postStageInit(primaryStage);

        primaryStage.setTitle("Net Monitor");
        FXUtil.setStageIcons(primaryStage);
        primaryStage.setScene(homeScene);
        primaryStage.setResizable(false);

        if (showStage) {
            primaryStage.show();
        }
    }

    @Override
    public void init() {
        try {
            try (final Connection conn = SQLiteFactory.getConnection()) {
                try (final Statement st = conn.createStatement()) {
                    st.execute("VACUUM");
                }
            }

            showStage = !Config.getBoolean(Keys.START_MINIMISED);

            Class.forName(ConnectionLogEntry.class.getName());
            Class.forName(Monitor.class.getName());

            loader = new FXMLLoader(getClass().getResource("/org/alorel/netmonitor/home.fxml"));
            homeScene = new Scene(loader.load());

            Tray.preStageInit();
        } catch (final Exception e) {
            FXDialog.exception(e, evt -> System.exit(1));
        }
    }
}
