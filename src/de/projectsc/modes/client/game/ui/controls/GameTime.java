/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.ui.controls;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.projectsc.core.data.utils.Timer;
import de.projectsc.modes.client.core.ui.UIElement;

public class GameTime extends UIElement {

    private Timer timer;

    public GameTime(Timer timer) {
        super("Game Timer", 0);
        this.timer = timer;
    }

    public String getCurrentTimeString() {
        Date date = new Date(timer.getGameTime());
        DateFormat formatter = null;
        if (timer.getGameTime() < 3600000) {
            formatter = new SimpleDateFormat("mm:ss");
        } else {
            formatter = new SimpleDateFormat("HH:mm:ss");
        }
        return formatter.format(date);
    }

}
