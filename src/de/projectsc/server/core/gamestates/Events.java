/*
 * Copyright (C) 2015
 */

package de.projectsc.server.core.gamestates;

import au.com.ds.ef.EventEnum;

public enum Events implements EventEnum {
    START_GAME_COMMAND, FINISHED_LOADING, GAME_ENDS, GAME_PAUSED
}
