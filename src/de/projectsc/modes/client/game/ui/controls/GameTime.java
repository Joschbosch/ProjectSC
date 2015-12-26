/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.ui.controls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.projectsc.modes.client.core.ui.UIElement;

public class GameTime extends UIElement {

    private long gameTime;

    public GameTime() {
        super("Game Timer", 0);
    }

    public String getCurrentTimeString() {
        Date date = new Date(gameTime);
        DateFormat formatter = null;
        if (gameTime < 3600000) {
            formatter = new SimpleDateFormat("mm:ss");
        } else {
            formatter = new SimpleDateFormat("HH:mm:ss");
        }
        return formatter.format(date);
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

}
