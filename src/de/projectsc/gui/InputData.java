/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.gui;

/**
 * Data format to saend input to the gui correct gui state.
 * 
 * @author Josch Bosch
 */
public class InputData {

    /**
     * 
     */
    public static final int TYPE_KEY = 0;

    /**
     * 
     */
    public static final int TYPE_MOUSE_KEY = 1;

    /**
     * 
     */
    public static final int TYPE_MOUSE_POSITION = 2;

    /**
     * 
     */
    public static final int TYPE_MOUSE_SCROLL = 4;

    private final int type;

    private final int keyOrButton;

    private final int action;

    private final int mods;

    private final double[] mousePosition;

    public InputData(int type, int button, int action, int mods, double[] mousePosition) {
        this.type = type;
        this.keyOrButton = button;
        this.action = action;
        this.mods = mods;
        this.mousePosition = mousePosition;
    }

    public int getType() {
        return type;
    }

    public int getKeyOrButton() {
        return keyOrButton;
    }

    public int getAction() {
        return action;
    }

    public int getMods() {
        return mods;
    }

    public double[] getMousePosition() {
        return mousePosition;
    }

}
