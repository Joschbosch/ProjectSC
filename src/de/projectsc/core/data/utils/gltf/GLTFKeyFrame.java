/*
 * Copyright (C) 2016 
 */
 
package de.projectsc.core.data.utils.gltf;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

public class GLTFKeyFrame {
    private float time = -1f;
    private Vector3f translation;
    private Vector3f scaling;
    private Quaternion orientation;
    
    
    public Vector3f getTranslation() {
        return translation;
    }

    
    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    
    public Vector3f getScaling() {
        return scaling;
    }

    
    public void setScaling(Vector3f scaling) {
        this.scaling = scaling;
    }

    
    public Quaternion getOrientation() {
        return orientation;
    }

    
    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }

    public void setTime(float time) {
        this.time = time;
    }
    
    public float getTime() {
        return time;
    }
}
