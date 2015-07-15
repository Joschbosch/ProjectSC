/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.server.core.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Configurations for a game.
 *
 * @author Josch Bosch
 */
public class GameConfiguration {

    private String mapName;

    private final Map<Long, String> playerCharacters;

    private final Map<Long, Byte> playerAffiliation;

    public GameConfiguration() {
        playerCharacters = new HashMap<>();
        playerAffiliation = new HashMap<>();
    }

    /**
     * 
     * @param id of the player
     * @param affiliation to set
     */
    public void setPlayerAffiliation(Long id, byte affiliation) {
        playerAffiliation.put(id, affiliation);

    }

    /**
     * @param id of the player
     * @return current affiliation
     */
    public byte getPlayerAffiliation(Long id) {
        return playerAffiliation.get(id);
    }

    /**
     * @param id of player
     * @param character chosen
     */
    public void setPlayerCharacter(Long id, String character) {
        playerCharacters.put(id, character);

    }

    /**
     * 
     * @param id of player
     * @return current selected character
     */
    public String getPlayerCharacter(Long id) {
        return playerCharacters.get(id);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     * @param affiliation to count
     * @return number of players with that affiliation
     */
    public int getAffiliationCount(byte affiliation) {
        byte count = 0;
        for (Byte b : playerAffiliation.values()) {
            if (b.byteValue() == affiliation) {
                count++;
            }
        }
        return count;
    }

}
