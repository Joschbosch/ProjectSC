/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.client.gui.events;

import java.io.File;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new texture should be applied.
 * 
 * @author Josch Bosch
 */
public class UpdateTextureEvent extends EntityEvent {

    private static final String NAME = UpdateTextureEvent.class.getName();

    private final File textureFile;

    public UpdateTextureEvent(String entityID, File textureFile) {
        super(NAME, entityID);
        this.textureFile = textureFile;
    }

    public File getTextureFile() {
        return textureFile;
    }

}
