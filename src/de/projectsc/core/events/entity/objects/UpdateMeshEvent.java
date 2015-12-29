/*
 * Copyright (C) 2015
 */

package de.projectsc.core.events.entity.objects;

import java.io.File;

import de.projectsc.core.data.EntityEvent;

/**
 * Event if a new mesh should be used.
 * 
 * @author Josch Bosch
 */
public class UpdateMeshEvent extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = UpdateMeshEvent.class.getName();

    private final File newMeshFile;

    public UpdateMeshEvent(String entityId, File newMeshFile) {
        super(ID, entityId);
        this.newMeshFile = newMeshFile;

    }

    public File getNewMeshFile() {
        return newMeshFile;
    }
}
