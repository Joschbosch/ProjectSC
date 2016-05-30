/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.util.ArrayList;
import java.util.List;

public class GLTFTrack {

    private String jointId;

    private String jointName;

    private GLTFJoint gltfJoint;

    private List<GLTFKeyFrame> keyframes;

    public GLTFTrack() {
        jointId = null;
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

    public String getJointId() {
        return jointId;
    }

    public void setJointId(String jointId) {
        this.jointId = jointId;
    }

    public String getJointName() {
        return jointName;
    }

    public void setJoint(GLTFJoint gltfJoint) {
        this.gltfJoint = gltfJoint;
    }
    public List<GLTFKeyFrame> getKeyframes(){
        return keyframes;
    }
    
}
