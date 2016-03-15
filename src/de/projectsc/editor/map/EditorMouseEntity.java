/*
 * Copyright (C) 2016 
 */

package de.projectsc.editor.map;

import de.projectsc.core.data.physics.Transform;
/**
 * For showing the entity that will be created at the mouse position.
 * @author Josch Bosch
 */
public class EditorMouseEntity {

    private Transform t = new Transform();

    private long type = 10000L;

    
    public Transform getT() {
        return t;
    }

    
    public void setT(Transform t) {
        this.t = t;
    }

    
    public long getType() {
        return type;
    }

    
    public void setType(long type) {
        this.type = type;
    }
    

}
