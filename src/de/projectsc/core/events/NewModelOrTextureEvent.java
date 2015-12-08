/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import java.io.File;

import de.projectsc.core.data.Event;

public class NewModelOrTextureEvent extends Event {

    private static final String NAME = "NewModelOrTextureEvent";

    private final File modelFile;

    private final File textureFile;

    public NewModelOrTextureEvent(long entityID, File modelFile, File textureFile) {
        super(NAME, entityID);
        this.modelFile = modelFile;
        this.textureFile = textureFile;
    }

    public File getModelFile() {
        return modelFile;
    }

    public File getTextureFile() {
        return textureFile;
    }

}
