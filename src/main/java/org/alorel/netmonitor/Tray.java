/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.alorel.netmonitor.util.SFX;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * System tray wrapper. You should first call {@link #preStageInit()} to initialise the bit of this class that does not
 * require a {@link Stage}, then call {@link #postStageInit(Stage)} when a Stage object is available.
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class Tray {

    /**
     * Icon for when the connection is up
     */
    private static Image imageUp;
    /**
     * Icon for when the connection is down
     */
    private static Image imageDown;

    /**
     * Message for when the connection is up
     */
    private static final String MSG_UP = "Connection is up";

    /**
     * Message for when the connection is down
     */
    private static final String MSG_DOWN = "Connection is down";

    /**
     * The tray icon.
     */
    private static TrayIcon trayIcon;

    /**
     * Perform some preliminary setup before a {@link Stage} object becomes available
     */
    public static void preStageInit() {
        imageUp = new ImageIcon(Tray.class.getResource("/org/alorel/netmonitor/up.png")).getImage();
        imageDown = new ImageIcon(Tray.class.getResource("/org/alorel/netmonitor/down.png")).getImage();

        trayIcon = new TrayIcon(imageDown, MSG_DOWN);
        trayIcon.setImageAutoSize(false);

        final Runnable shutdownRunnable = () -> {
            try {
                SystemTray.getSystemTray().remove(trayIcon);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownRunnable));

        final MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(e -> {
            shutdownRunnable.run();
            System.exit(1);
        });

        final PopupMenu popupMenu = new PopupMenu("Net Monitor");
        popupMenu.add(exit);

        trayIcon.setPopupMenu(popupMenu);

        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (final AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the connection state to "down"
     */
    public static void setDown() {
        System.out.println("Connection went down");
        trayIcon.setImage(imageDown);
        trayIcon.setToolTip(MSG_DOWN);
        toast(MSG_DOWN, TrayIcon.MessageType.ERROR);
        SFX.defaultDown();
    }

    /**
     * Set the connection state to "up"
     */
    public static void setUp() {
        System.out.println("Connection went up");
        trayIcon.setImage(imageUp);
        trayIcon.setToolTip(MSG_UP);
        toast(MSG_UP, TrayIcon.MessageType.INFO);
        SFX.defaultUp();
    }

    /**
     * Show a toast message
     *
     * @param message     The message
     * @param messageType The message type
     */
    public static void toast(final String message, final TrayIcon.MessageType messageType) {
        trayIcon.displayMessage("Net Monitor", message, messageType);
    }

    /**
     * Finalise class setup
     *
     * @param primaryStage The primary stage of the application
     */
    public static void postStageInit(final Stage primaryStage) {
        final Runnable showHideRunnable = () -> {
            if (primaryStage.isShowing()) {
                primaryStage.hide();
            } else {
                primaryStage.show();
            }
        };

        final MenuItem showHideMenu = new MenuItem("Show/hide");
        showHideMenu.addActionListener(e -> Platform.runLater(showHideRunnable));

        trayIcon.addMouseListener(new MouseListener() {
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
    }
}
