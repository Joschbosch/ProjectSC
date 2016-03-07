/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.events.entity.actions;

import de.projectsc.core.data.EntityEvent;

/**
 * Action that a basic attack started.
 * 
 * @author Josch Bosch
 */
public class BasicAttackEntityAction extends EntityEvent {

    /**
     * ID.
     */
    public static final String ID = BasicAttackEntityAction.class.getName();

    private String targetEntity;

    public BasicAttackEntityAction(String entityID, String targetEntity) {
        super(ID, entityID);
        this.setTargetEntity(targetEntity);
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }
}
