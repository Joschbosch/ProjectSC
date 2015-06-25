/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.client.gui;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

/**
 * Timer for the GUI.
 * 
 * @author Josch Bosch
 */
public class Timer {

    private static final int SECONDS_CONSTANT = 1000;

    /**
     * Frames per second.
     */
    private int fps;

    private long lastFrame;

    private long lastFPS;

    /**
     * Initializes the timer.
     */
    public void init() {
        lastFPS = getTime(); // call before loop to initialise fps timer
        getDelta(); // call once before loop to initialise lastFrame
    }

    /**
     * Calculate how many milliseconds have passed since last frame.
     * 
     * @return milliseconds passed since last frame
     */
    public int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;

        return delta;
    }

    /**
     * Get the accurate system time.
     * 
     * @return The system time in milliseconds
     */
    public long getTime() {
        return (Sys.getTime() * SECONDS_CONSTANT) / Sys.getTimerResolution();
    }

    /**
     * Calculate the FPS and set it in the title bar.
     */
    public void updateFPS() {
        if (getTime() - lastFPS > SECONDS_CONSTANT) {
            Display.setTitle("FPS: " + fps);
            fps = 0;
            lastFPS += SECONDS_CONSTANT;
        }
        fps++;
    }
}
