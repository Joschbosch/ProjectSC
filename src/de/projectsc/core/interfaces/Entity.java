/*
 * Copyright (C) 2015 
 */

package de.projectsc.core.interfaces;

import de.projectsc.core.data.physics.Transform;

public interface Entity {

    String getID();

    Transform getTransform();

    boolean hasComponent(Class<? extends Component> clazz);

    Component getComponent(Class<? extends Component> clazz);

    long getEntityTypeId();

    void setEntityTypeId(long id);

}
