/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.events;

import java.io.File;

import de.projectsc.core.data.Event;

/**
 * Event if a new texture should be applied.
 * 
 * @author Josch Bosch
 */
public class NewTextureEvent extends Event {

    private static final String NAME = "NewTextureEvent";

    private final File textureFile;

    public NewTextureEvent(long entityID, File textureFile) {
        super(NAME, entityID);
        this.textureFile = textureFile;
    }

    public File getTextureFile() {
        return textureFile;
    }

}
