/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

/**
 * Interface for all ui overlays, that are moveable.
 * 
 * @author Josch Bosch
 */
public interface OverlayMoveable extends Overlay {

    /**
     * @return true, if element is in moving state.
     */
    boolean isMoving();

    /**
     * Do the moving of the overlay.
     */
    void move();
}
