/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.objects;

import java.io.File;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new mesh should be used.
 * 
 * @author Josch Bosch
 */
public class NewMeshEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = "NewMeshEvent";

    private final File newMeshFile;

    public NewMeshEvent(String entityId, File newMeshFile) {
        super(ID, entityId);
        this.newMeshFile = newMeshFile;

    }

    public File getNewMeshFile() {
        return newMeshFile;
    }
}
