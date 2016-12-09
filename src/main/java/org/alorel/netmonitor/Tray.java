/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor;

import javafx.application.Platform;
import javafx.stage.Stage;

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
 * Created by Art on 09/12/2016.
 */
public class Tray {

    private static Image imageUp;
    private static Image imageDown;

    private static final String MSG_UP = "Connection is up";
    private static final String MSG_DOWN = "Connection is down";

    private static TrayIcon trayIcon;

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

    public static void setDown() {
        System.out.println("Connection went down");
        trayIcon.setImage(imageDown);
        trayIcon.setToolTip(MSG_DOWN);
        toast(MSG_DOWN, TrayIcon.MessageType.ERROR);
    }

    public static void setUp() {
        System.out.println("Connection went up");
        trayIcon.setImage(imageUp);
        trayIcon.setToolTip(MSG_UP);
        toast(MSG_UP, TrayIcon.MessageType.INFO);
    }

    public static void toast(final String message, final TrayIcon.MessageType messageType) {
        trayIcon.displayMessage("Net Monitor", message, messageType);
    }

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
