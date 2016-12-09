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
import org.alorel.netmonitor.Monitor;
import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;
import org.alorel.netmonitor.util.FXDialog;
import org.alorel.netmonitor.util.IOUtil;

import java.awt.Desktop;
import java.net.URI;
import java.text.DateFormat;

/**
 * Created by Art on 09/12/2016.
 */
public class HomeController {

    @FXML
    private Label labelMonitorStatus;

    @FXML
    private CheckBox cbStartMinimised;

    @FXML
    private CheckBox cbSFXOn;

    @FXML
    private CheckBox cbLogConnect;

    @FXML
    private Text netMonitorVersion;

    @FXML
    private void initialize() {
        setCurrentConfig();
        initUIChangeListener();

        netMonitorVersion.setText(IOUtil.readBundledFile("/VERSION"));
    }

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

    private void setCurrentConfig() {
        cbStartMinimised.setSelected(Config.getBoolean(Keys.START_MINIMISED));
        cbLogConnect.setSelected(Config.getBoolean(Keys.LOG_CONNECT_STATUS));
        cbSFXOn.setSelected(Config.getBoolean(Keys.SOUND_ENABLED));
    }

    @FXML
    private void changeMinimised() {
        Config.set(Keys.START_MINIMISED, cbStartMinimised.isSelected());
    }

    @FXML
    private void changeSFXOn() {
        Config.set(Keys.SOUND_ENABLED, cbSFXOn.isSelected());
    }

    @FXML
    private void changeLog() {
        Config.set(Keys.LOG_CONNECT_STATUS, cbLogConnect.isSelected());
    }

    @FXML
    private void clickHomepage() {
        openURL("https://github.com/Alorel/netmonitor");
    }

    private static void openURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (final Exception e) {
            FXDialog.exception(e);
        }
    }

    @FXML
    private void clickLabeledLink(final ActionEvent evt) {
        final String url = ((Hyperlink) evt.getTarget()).getText();
        openURL(url);
    }
}
