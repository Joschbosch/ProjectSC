/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import java.util.ArrayList;
import java.util.List;

public class GLTFTrack {

    private int jointId;

    private String jointName;

    private Joint gltfJoint;

    private List<GLTFKeyFrame> keyframes;

    public GLTFTrack() {
        jointId = -1;
        jointName = null;
        gltfJoint = null;
        keyframes = new ArrayList<>();
    }
    
    public void addKeyframe(GLTFKeyFrame frame){
        keyframes.add(frame);
    }

    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    public int getJointId() {
        return jointId;
    }

    public void setJointId(int i) {
        this.jointId = i;
    }

    public String getJointName() {
        return jointName;
    }

    public void setJoint(Joint gltfJoint) {
        this.gltfJoint = gltfJoint;
    }
    public List<GLTFKeyFrame> getKeyframes(){
        return keyframes;
    }
    
}
