/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui.util;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Dialog utilities
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class FXDialog {

    /**
     * Creates an error dialog
     *
     * @param content Message to display
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> error(final String content) {
        return error(content, null);
    }

    /**
     * Creates an error dialog
     *
     * @param content  Message to display
     * @param onHiding Handler called when the dialog begins hiding
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> error(final String content, @Nullable final EventHandler<DialogEvent> onHiding) {
        return message("Error", content, Alert.AlertType.ERROR, onHiding);
    }

    /**
     * Creates an information dialog
     *
     * @param content Message to display
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> info(final String content) {
        return info(content, null);
    }

    /**
     * Creates an information dialog
     *
     * @param content  Message to display
     * @param onHiding Handler called when the dialog begins hiding
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> info(final String content, @Nullable final EventHandler<DialogEvent> onHiding) {
        return message("Info", content, Alert.AlertType.INFORMATION, onHiding);
    }

    /**
     * Creates a warning dialog
     *
     * @param content Message to display
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> warn(final String content) {
        return warn(content, null);
    }

    /**
     * Creates a warning dialog
     *
     * @param content  Message to display
     * @param onHiding Handler called when the dialog begins hiding
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> warn(final String content, @Nullable final EventHandler<DialogEvent> onHiding) {
        return message("Warning", content, Alert.AlertType.WARNING, onHiding);
    }

    /**
     * Prompts for user confirmation
     *
     * @param text The text to display
     * @return true if the user clicked yes, false if not
     */
    public static boolean confirm(final String text) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(text);

        FXUtil.setStageIcons((Stage) alert.getDialogPane().getScene().getWindow());

        final Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && ButtonType.OK.equals(result.get());
    }

    /**
     * Abstract dialog creator
     *
     * @param header   The header text to display
     * @param content  The main text
     * @param type     Dialog type
     * @param onHiding Handler called when the dialog begins hiding
     * @return An empty Optional if called outside JavaFX or an optional containing the button type clicked if called from
     * within JavaFX
     */
    private static Optional<ButtonType> message(
            final String header,
            final String content,
            final Alert.AlertType type,
            @Nullable final EventHandler<DialogEvent> onHiding
    ) {
        final RunnableCallable<Optional<ButtonType>> rc = () -> {
            final Alert alert = new Alert(type);
            alert.setTitle(header);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.initModality(Modality.APPLICATION_MODAL);

            FXUtil.setStageIcons((Stage) alert.getDialogPane().getScene().getWindow());

            if (null != onHiding) {
                alert.setOnHiding(onHiding);
            }

            return alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            return rc.call();
        } else {
            Platform.runLater(rc);
        }

        return Optional.empty();
    }

    /**
     * Creates a exception dialog
     *
     * @param e The exception to display
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> exception(final Throwable e) {
        return exception(e, null);
    }

    /**
     * Creates a exception dialog
     *
     * @param e        The exception to display
     * @param onHiding Handler called when the dialog begins hiding
     * @return See {@link #message(String, String, Alert.AlertType, EventHandler)}
     */
    public static Optional<ButtonType> exception(final Throwable e, @Nullable final EventHandler<DialogEvent> onHiding) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();

        final String exceptionText = sw.toString();

        final RunnableCallable<Optional<ButtonType>> rc = () -> {
            final Label label = new Label("Stack trace:");

            final TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            final GridPane expContent = new GridPane();
            expContent.setMaxWidth(1.7976931348623157E308D);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Error");
            alert.setHeaderText("An exception occurred");
            alert.setContentText(e.getLocalizedMessage());

            FXUtil.setStageIcons((Stage) alert.getDialogPane().getScene().getWindow());

            if (null != onHiding) {
                alert.setOnHiding(onHiding);
            }

            alert.getDialogPane().setExpandableContent(expContent);

            return alert.showAndWait();
        };

        if (Platform.isFxApplicationThread()) {
            return rc.call();
        } else {
            Platform.runLater(rc);
        }

        return Optional.empty();
    }
}