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
import org.alorel.netmonitor.common.Monitor;
import org.alorel.netmonitor.common.Tray;
import org.alorel.netmonitor.common.sqlite.SQLiteFactory;
import org.alorel.netmonitor.common.sqlite.config.Config;
import org.alorel.netmonitor.common.sqlite.config.Keys;
import org.alorel.netmonitor.common.sqlite.connectionlog.ConnectionLogEntry;
import org.alorel.netmonitor.common.updatecheck.UpdateChecker;
import org.alorel.netmonitor.common.updatecheck.UpdateStatus;
import org.alorel.netmonitor.gui.util.FXDialog;
import org.alorel.netmonitor.gui.util.FXUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.Desktop;
import java.awt.MenuItem;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

/**
 * The main JavaFX application
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class GUIApp extends Application {

    /**
     * The main scene
     */
    private Scene homeScene;

    /**
     * Whether to show the stage when loading completes, i.e. the "start minimised" option
     */
    private boolean showStage;

    private void postStageTrayInit(final Stage primaryStage) {
        final MenuItem showHideMenu = new MenuItem(showStage ? "Hide" : "Show");

        final Runnable showHideRunnable = () -> {
            if (primaryStage.isShowing()) {
                primaryStage.hide();
            } else {
                primaryStage.show();
            }
        };

        primaryStage.setOnHidden(e -> showHideMenu.setLabel("Show"));
        primaryStage.setOnShown(e -> showHideMenu.setLabel("Hide"));

        showHideMenu.addActionListener(e -> Platform.runLater(showHideRunnable));

        Tray.getTrayIcon().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (MouseEvent.BUTTON1 == e.getButton()) {
                    Platform.runLater(showHideRunnable);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        Tray.getTrayIcon().getPopupMenu().add(showHideMenu);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        postStageTrayInit(primaryStage);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

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
            //Prepare tray icon
            Tray.init();

            // Compact the database
            try (final Connection conn = SQLiteFactory.getConnection()) {
                try (final Statement st = conn.createStatement()) {
                    st.execute("VACUUM");
                }
            }

            //Check whether we're starting in minimised mode
            showStage = !Config.getBoolean(Keys.START_MINIMISED);

            //Initialise classes
            Class.forName(ConnectionLogEntry.class.getName());
            Class.forName(Monitor.class.getName());

            //Load main UI
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/alorel/netmonitor/home.fxml"));
            homeScene = new Scene(loader.load());
        } catch (final Exception e) {
            Platform.runLater(() -> FXDialog.exception(e, evt -> System.exit(1)));
        }
    }
}
