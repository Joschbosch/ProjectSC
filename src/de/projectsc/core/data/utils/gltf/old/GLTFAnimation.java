/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf.old;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GLTFAnimation {

    private Map<String, GLTFTrack> tracks;

    private float duration;

    private int frameCount;

    public GLTFAnimation() {
        tracks = new HashMap<>();
        duration = -1f;
    }

    public void addTrack(GLTFTrack track) {
        tracks.put(track.getJointName(), track);
    }

    public Map<String, GLTFTrack> getTracks() {
        return tracks;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float time) {
        duration = time;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public GLTFTrack getTrackFromJoint(Joint joint) {
        return tracks.get(joint.getJointName());
    }
}
