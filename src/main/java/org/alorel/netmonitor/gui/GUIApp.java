/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.alorel.netmonitor.Monitor;
import org.alorel.netmonitor.Tray;
import org.alorel.netmonitor.sqlite.SQLiteFactory;
import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;
import org.alorel.netmonitor.sqlite.connectionlog.ConnectionLogEntry;
import org.alorel.netmonitor.updatecheck.UpdateChecker;
import org.alorel.netmonitor.updatecheck.UpdateStatus;
import org.alorel.netmonitor.util.FXDialog;
import org.alorel.netmonitor.util.FXUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.Desktop;
import java.net.URI;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

/**
 * Created by Art on 09/12/2016.
 */
@ParametersAreNonnullByDefault
public class GUIApp extends Application {

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

        final Thread updateCheckThread = new Thread(() -> {
            if (UpdateChecker.hasUpdate() == UpdateStatus.NEW_VERSION_AVAILABLE) {
                Platform.runLater(() -> {
                    final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    FXUtil.setStageIcons((Stage) alert.getDialogPane().getScene().getWindow());

                    alert.setTitle("Net Monitor Update");
                    alert.setHeaderText("Net Monitor Update");
                    alert.setContentText("A new version of the application is available. Would you like to open the "
                            + "download page?");

                    final ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                    final ButtonType noBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(yesBtn, noBtn);

                    final Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == yesBtn) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://github.com/Alorel/netmonitor/releases"));
                        } catch (final Exception e) {
                            FXDialog.exception(e);
                        }
                    }
                });
            }
        });
        updateCheckThread.setPriority(Thread.MIN_PRIORITY);
        updateCheckThread.start();
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

            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/alorel/netmonitor/home.fxml"));
            homeScene = new Scene(loader.load());

            Tray.preStageInit();
        } catch (final Exception e) {
            FXDialog.exception(e, evt -> System.exit(1));
        }
    }
}
