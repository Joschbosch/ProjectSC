/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.events;

import java.io.File;

import de.projectsc.core.data.Event;

public class NewMeshEvent extends Event {

    public static final String ID = "NewMeshEvent";

    private File newMeshFile;

    public NewMeshEvent(long entityId, File newMeshFile) {
        super(ID, entityId);
        this.newMeshFile = newMeshFile;

    }

    public File getNewMeshFile() {
        return newMeshFile;
    }
}
