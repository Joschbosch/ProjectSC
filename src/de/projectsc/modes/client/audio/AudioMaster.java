/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.audio;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

/**
 * The main class for the audio system.
 * 
 * @author Josch Bosch
 */
public final class AudioMaster {

    protected static final Log LOGGER = LogFactory.getLog(AudioMaster.class);

    private static List<Integer> buffers = new ArrayList<>();

    private AudioMaster() {

    }
    /**
     * Initialize audio system.
     */
    public static void init() {
        try {
            AL.create();
        } catch (LWJGLException e) {
            LOGGER.error(e.getStackTrace());
        }
    }
    /**
     * Set listener config.
     */
    public static void setListenerData() {
        AL10.alListener3f(AL10.AL_POSITION, 0, 0, 0);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }
    /**
     * Load a sound from a file into a buffer. 
     * @param file to load
     * @return id of buffer where the file is stored.
     */
    public static int loadSound(String file) {
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);
        WaveData waveFile = WaveData.create(AudioMaster.class.getResourceAsStream("/audio/" + file));
        AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();
        return buffer;
    }
    /**
     * Dispose audio system.
     */
    public static void dispose() {
        for (int buffer : buffers) {
            AL10.alDeleteBuffers(buffer);
        }
        AL.destroy();
    }
}
