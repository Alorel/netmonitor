/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.alorel.netmonitor.util.FXDialog;
import org.alorel.netmonitor.util.FXUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.SystemTray;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Main entry class
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class GUIPreloader extends Preloader {

    private Stage preloaderStage;

    @Override
    public void start(final Stage st) throws Exception {
        Thread.currentThread().setName("JavaFX");

        if (SystemTray.isSupported()) {
            try {
                setupCustomTooltipBehavior(0, Integer.MAX_VALUE, 0);
            } catch (final ReflectiveOperationException e) {
                System.err.println("Failed to edit default tooltip behaviour");
                e.printStackTrace();
            }

            preloaderStage = st;
            Platform.setImplicitExit(false);
            FXUtil.setStageIcons(st);
            st.setResizable(false);

            final VBox vbox = new VBox(2);
            vbox.setMaxWidth(Region.USE_PREF_SIZE);
            vbox.setMaxHeight(Region.USE_PREF_SIZE);
            vbox.setAlignment(Pos.CENTER);

            final Label label = new Label("Net Monitor loading...");

            vbox.getChildren().add(label);

            final Scene scene = new Scene(new BorderPane(vbox));

            st.setTitle("Net Monitor");
            st.setWidth(250);
            st.setHeight(125);
            st.setScene(scene);
            st.show();
        } else {
            FXDialog.error("Your operating system or desktop environment does not support the System Tray; "
                    + "the application cannot function", e -> System.exit(1));
        }
    }

    public static void main(final String[] args) {
        LauncherImpl.launchApplication(GUIApp.class, GUIPreloader.class, args);
    }

    /**
     * Sets up custom tooltip delays
     *
     * @param openDelay       Delay before a tooltip is shown
     * @param visibleDuration Duration of tooltip visibility
     * @param closeDelay      Delay before a tooltip is closed
     * @throws ReflectiveOperationException If we failed
     */
    private static void setupCustomTooltipBehavior(
            final int openDelay,
            final int visibleDuration,
            final int closeDelay
    ) throws ReflectiveOperationException {
        Class TTBehaviourClass = null;
        final String requiredClass = "javafx.scene.control.Tooltip.TooltipBehavior";

        for (final Class c : Tooltip.class.getDeclaredClasses()) {
            if (c.getCanonicalName().equals(requiredClass)) {
                TTBehaviourClass = c;
                break;
            }
        }

        if (null != TTBehaviourClass) {
            @SuppressWarnings("unchecked")
            final Constructor constructor = TTBehaviourClass.getDeclaredConstructor(
                    Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);

            final Object newTTBehaviour = constructor.newInstance(
                    new Duration(openDelay), new Duration(visibleDuration),
                    new Duration(closeDelay), false);

            final Field ttbehaviourField = Tooltip.class.getDeclaredField("BEHAVIOR");
            ttbehaviourField.setAccessible(true);

            ttbehaviourField.set(Tooltip.class, newTTBehaviour);
        } else {
            System.err.printf("Could not find %s - can't modify tooltip behaviour%n", requiredClass);
        }
    }

    /**
     * State change handler
     *
     * @param stateChangeNotification The state change notification
     */
    @Override
    public void handleStateChangeNotification(final StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }
}
