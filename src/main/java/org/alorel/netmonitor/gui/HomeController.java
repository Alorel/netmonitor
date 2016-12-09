/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.alorel.netmonitor.Monitor;
import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;

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
    private CheckBox cbLogConnect;

    @FXML
    private void initialize() {
        setCurrentConfig();
        initUIChangeListener();
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
    }

    @FXML
    private void changeMinimised() {
        Config.set(Keys.START_MINIMISED, cbStartMinimised.isSelected());
    }

    @FXML
    private void changeLog() {
        Config.set(Keys.LOG_CONNECT_STATUS, cbLogConnect.isSelected());
    }
}
