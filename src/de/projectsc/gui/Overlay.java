/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

/**
 * Interface for all UI overlays.
 * 
 * @author Josch Bosch
 */
public interface Overlay {

    /**
     * Render overlay.
     */
    void render();

    /**
     * Check whether overlay is at position (x,y).
     * 
     * @param x coordinate
     * @param y coordinate
     * @return true, if input should be handles by overlay.
     */
    boolean isAtPostion(int x, int y);

    /**
     * Handle current input.
     */
    void handleInput();
}
