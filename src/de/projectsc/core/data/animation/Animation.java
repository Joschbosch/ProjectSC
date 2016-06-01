/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.animation;

import java.util.HashMap;
import java.util.Map;

public class Animation {

    private Map<String, Track> tracks;

    private float duration;

    private int frameCount;

    private Skeleton skeleton;

    public Animation() {
        tracks = new HashMap<>();
        duration = -1f;
    }

    public void addTrack(Track track) {
        tracks.put(track.getJointName(), track);
    }

    public Map<String, Track> getTracks() {
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

    public Track getTrackFromJoint(Joint joint) {
        return tracks.get(joint.getName());
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }
}
