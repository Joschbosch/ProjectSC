/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data;

import java.util.LinkedList;
import java.util.List;

import de.projectsc.core.data.physics.WireFrame;

/**
 * Describes the scene without any gui objects.
 *
 * @author Josch Bosch
 */
public class Scene {

    private boolean wireframeEnabled = true;

    private boolean isDebugMode = true;

    private List<WireFrame> wireFrames = null;

    public Scene() {
        wireFrames = new LinkedList<>();
    }

    public boolean isWireframeEnable() {
        return wireframeEnabled;
    }

    public void setWireframeEnabled(boolean wireframeEnabled) {
        this.wireframeEnabled = wireframeEnabled;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.isDebugMode = debugMode;
    }

    public List<WireFrame> getWireFrames() {
        return wireFrames;
    }
}
