/*
 * Copyright (C) 2015 
 */

package de.projectsc.modes.client.game.data;

/**
 * Game context on client side.
 * 
 * @author Josch Bosch
 */
public class ClientGameContext {

    private String mapName = "L1/first.map";

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }
}
