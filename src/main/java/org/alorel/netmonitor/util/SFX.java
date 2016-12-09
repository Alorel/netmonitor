/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.util;

import org.alorel.netmonitor.sqlite.config.Config;
import org.alorel.netmonitor.sqlite.config.Keys;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.IOException;

/**
 * Created by Art on 09/12/2016.
 */
public class SFX {

    public static void play(final String url) {
        if (Config.getBoolean(Keys.SOUND_ENABLED)) {
            try {
                final AudioInputStream ais = AudioSystem.getAudioInputStream(SFX.class.getResource(url));
                final Clip clip = AudioSystem.getClip();

                clip.addLineListener(event -> {
                    if (LineEvent.Type.STOP.equals(event.getType())) {
                        try {
                            ais.close();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        clip.close();
                    }
                });

                clip.open(ais);
                clip.start();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void defaultDown() {
        play("/org/alorel/netmonitor/error.wav");
    }

    public static void defaultUp() {
        play("/org/alorel/netmonitor/success.wav");
    }
}
