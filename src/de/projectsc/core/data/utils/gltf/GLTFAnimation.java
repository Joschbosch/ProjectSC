/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.gltf;

import java.util.ArrayList;
import java.util.List;

public class GLTFAnimation {

    private List<GLTFTrack> tracks;
    private float duration;

    public GLTFAnimation() {
        tracks = new ArrayList<>();
        duration = -1f;
    }

    public void addTrack(GLTFTrack track) {
        tracks.add(track);        
        if (duration == -1f){
            track.getKeyframes().get(track.getKeyframes().size()-1).getTime();
        }
    }
}
