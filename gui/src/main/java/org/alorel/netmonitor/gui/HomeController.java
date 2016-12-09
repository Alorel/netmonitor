/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.alorel.netmonitor.common.Monitor;
import org.alorel.netmonitor.common.sqlite.config.Config;
import org.alorel.netmonitor.common.sqlite.config.Keys;
import org.alorel.netmonitor.common.updatecheck.Version;
import org.alorel.netmonitor.gui.util.FXDialog;

import java.awt.Desktop;
import java.net.URI;
import java.text.DateFormat;

/**
 * Main UI controller
 *
 * @author a.molcanovas@gmail.com
 */
public class HomeController {

    /**
     * Current link state
     */
    @FXML
    private Label labelMonitorStatus;

    /**
     * Config checkbox for "start minimised"
     */
    @FXML
    private CheckBox cbStartMinimised;

    /**
     * Config checkbox for "enable sound"
     */
    @FXML
    private CheckBox cbSFXOn;

    /**
     * Config checkbox for "log connect/disconnect events"
     */
    @FXML
    private CheckBox cbLogConnect;

    /**
     * The current application version
     */
    @FXML
    private Text netMonitorVersion;

    /**
     * Initialiser
     */
    @FXML
    private void initialize() {
        setCurrentConfig();
        initUIChangeListener();

        netMonitorVersion.setText(Version.CURRENT.getVersion());
    }

    /**
     * Init the state change listener that will update the UI, as well as the monitor itself
     */
    private void initUIChangeListener() {
        final ReadOnlyBooleanProperty isUp = Monitor.getUptimeProperty();
        final String format = "%s since %s";

        isUp.addListener((obs, oldValue, newValue) -> {
            final String text = String.format(
                    format,
                    isUp.get() ? "Up" : "Down",
                    DateFormat.getDateTimeInstance().format(Monitor.getChangeDate())
            );

            Platform.runLater(() -> labelMonitorStatus.setText(text));
        });

        Monitor.init();
    }

    /**
     * Set the current configuration values
     */
    private void setCurrentConfig() {
        cbStartMinimised.setSelected(Config.getBoolean(Keys.START_MINIMISED));
        cbLogConnect.setSelected(Config.getBoolean(Keys.LOG_CONNECT_STATUS));
        cbSFXOn.setSelected(Config.getBoolean(Keys.SOUND_ENABLED));
    }

    /**
     * Change handler for the "start minimised" option
     */
    @FXML
    private void changeMinimised() {
        Config.set(Keys.START_MINIMISED, cbStartMinimised.isSelected());
    }

    /**
     * Change handler for the "enable sound" option
     */
    @FXML
    private void changeSFXOn() {
        Config.set(Keys.SOUND_ENABLED, cbSFXOn.isSelected());
    }

    /**
     * Change handler for the "enable state logging" option
     */
    @FXML
    private void changeLog() {
        Config.set(Keys.LOG_CONNECT_STATUS, cbLogConnect.isSelected());
    }

    /**
     * Open the app homepage
     */
    @FXML
    private void clickHomepage() {
        openURL("https://github.com/Alorel/netmonitor");
    }

    /**
     * Helper class to open a URL
     *
     * @param url The URL
     */
    private static void openURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (final Exception e) {
            FXDialog.exception(e);
        }
    }

    /**
     * Click handler for a {@link Hyperlink} that has its URL set as its text
     *
     * @param evt The click event
     */
    @FXML
    private void clickLabeledLink(final ActionEvent evt) {
        final String url = ((Hyperlink) evt.getTarget()).getText();
        openURL(url);
    }
}
