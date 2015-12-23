/*
 * Copyright (C) 2015
 */

package de.projectsc.core.data.utils;

/**
 * This util class is used to calculate the time that has passed in a frame.
 * 
 * @author Josch Bosch
 */
public final class Timer {

    private static long lastSnapshotTime;

    private static long lag;

    private static long delta;

    private static long snapshotTime;

    private static long frameCount = 0;

    private static long fpsTimer = 0;

    private static long currentFPS = 0;

    private Timer() {}

    /**
     * Initialize the timer.
     */
    public static void init() {
        lastSnapshotTime = System.currentTimeMillis();
        snapshotTime = lastSnapshotTime;
        lag = 0;
    }

    /**
     * Update time for the current frame.
     */
    public static void update() {
        snapshotTime = System.currentTimeMillis();
        delta = snapshotTime - lastSnapshotTime;
        lastSnapshotTime = snapshotTime;
        lag += delta;
        frameCount++;
        fpsTimer += delta;
        if (fpsTimer > 1000) {
            currentFPS = frameCount;
            frameCount = 0;
            fpsTimer = 0;
        }
    }

    public static long getCurrentFPS() {
        return currentFPS;
    }

    public static long getLag() {
        return lag;
    }

    public static void setLag(long l) {
        lag = l;
    }

    public static long getDelta() {
        return delta;
    }

    public static long getSnapshotTime() {
        return snapshotTime;
    }

}
