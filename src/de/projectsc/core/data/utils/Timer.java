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

    private long lastSnapshotTime;

    private long lag;

    private long delta;

    private long snapshotTime;

    private long frameCount = 0;

    private long fpsTimer = 0;

    private long currentFPS = 0;

    private long tick = 0;

    private long gameTime = 0;

    private long measureMent;

    /**
     * Initialize the timer.
     */
    public void init() {
        lastSnapshotTime = System.currentTimeMillis();
        snapshotTime = lastSnapshotTime;
        lag = 0;
        tick = 0;
        gameTime = 0;
    }

    /**
     * Update time for the current frame.
     */
    public void update() {
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

    public long getCurrentFPS() {
        return currentFPS;
    }

    public long getLag() {
        return lag;
    }

    public void setLag(long l) {
        lag = l;
    }

    public long getDelta() {
        return delta;
    }

    public long getSnapshotTime() {
        return snapshotTime;
    }

    public long getTick() {
        return tick;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void updateGameTimeAndTick(long gameTickTime) {
        gameTime += gameTickTime;
        tick++;
    }

    public void start() {
        measureMent = System.currentTimeMillis();
    }

    public long stop() {
        return System.currentTimeMillis() - measureMent;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

}
