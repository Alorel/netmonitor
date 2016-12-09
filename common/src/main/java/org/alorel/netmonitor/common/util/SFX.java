/*
 * Copyright (c) 2016 Arturas Molcanovas.
 *
 * This software is licenced under the MIT license. Please see bundled license file for more information.
 */

package org.alorel.netmonitor.common.util;

import org.alorel.netmonitor.common.sqlite.config.Config;
import org.alorel.netmonitor.common.sqlite.config.Keys;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.IOException;

/**
 * Plays sound effects
 *
 * @author a.molcanovas@gmail.com
 */
@ParametersAreNonnullByDefault
public class SFX {

    /**
     * Plays the given sound effect if sound effects are enabled
     *
     * @param url Path to the sound effect
     */
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

    /**
     * Plays the default "connection down" sound effect
     */
    public static void defaultDown() {
        play("/org/alorel/netmonitor/error.wav");
    }

    /**
     * Plays the default "connection up" sound effect
     */
    public static void defaultUp() {
        play("/org/alorel/netmonitor/success.wav");
    }
}
