/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.ui.elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.projectsc.modes.client.core.states.UIElementConstants;
import de.projectsc.modes.client.ui.BasicUIElement;

public class GameTime extends BasicUIElement {

    private long gameTime;

    public GameTime() {
        super(UIElementConstants.GAMETIMER, 0);
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
