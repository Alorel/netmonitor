/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.gui.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * JavaFX utilities
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class FXUtil {

    /**
     * Default stage icons
     */
    private final static Image[] defaultStageIcons = new Image[]{
            new Image(FXUtil.class.getResource("/org/alorel/netmonitor/up.png").toExternalForm())
    };

    /**
     * Sets the default icons on a stage
     *
     * @param st The target stage
     * @return The given stage
     */
    public static Stage setStageIcons(final Stage st) {
        st.getIcons().setAll(defaultStageIcons);

        return st;
    }
}
