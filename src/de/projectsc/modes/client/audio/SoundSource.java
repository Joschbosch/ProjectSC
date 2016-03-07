/*
 * Copyright (C) 2016 
 */

package de.projectsc.modes.client.audio;

import org.lwjgl.openal.AL10;

/**
 * Source for a sound to play.
 * 
 * @author Josch Bosch
 */
public class SoundSource {

    private int sourceId;

    public SoundSource() {
        sourceId = AL10.alGenSources();
        AL10.alSourcef(sourceId, AL10.AL_GAIN, 3);
        AL10.alSourcef(sourceId, AL10.AL_PITCH, 2);
        AL10.alSource3f(sourceId, AL10.AL_POSITION, 0, 0, 0);

    }

    /**
     * Play the given buffer with the current source.
     * 
     * @param buffer id to play
     */
    public void play(int buffer) {
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
        AL10.alSourcePlay(sourceId);
    }
    /**
     * Delete the source.
     */
    public void delete() {
        AL10.alDeleteSources(sourceId);
    }
}
