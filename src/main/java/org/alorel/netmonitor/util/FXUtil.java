/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.Desktop;
import java.net.URI;

/**
 * Created by Art on 09/12/2016.
 */
@ParametersAreNonnullByDefault
public class FXUtil {

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

    public static void openExternalLink(final String link) throws RuntimeException {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
