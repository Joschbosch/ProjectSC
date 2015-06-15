/*
 * Copyright (C) 2015 
 */

package de.projectsc.gui;

public interface Drawable {

    void render();

    boolean isAtPostion(int x, int y);

    void handleInput();
}
