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

    private static int lag;

    private static long delta;

    private static long snapshotTime;

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
    }

    public static int getLag() {
        return lag;
    }

    public static void setLag(int newLag) {
        lag = newLag;
    }

    public static long getDelta() {
        return delta;
    }

    public static long getSnapshotTime() {
        return snapshotTime;
    }

}
