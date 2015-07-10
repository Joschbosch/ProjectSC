/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import java.util.HashMap;
import java.util.Map;

public class GameConfiguration {

    private String mapName;

    private Map<Long, String> playerCharacters;

    private Map<Long, Byte> playerAffiliation;

    public GameConfiguration() {
        playerCharacters = new HashMap<>();
        playerAffiliation = new HashMap<>();
    }

    public void setPlayerAffiliation(Long id, byte affiliation) {
        playerAffiliation.put(id, affiliation);

    }

    public byte getPlayerAffiliation(Long id) {
        return playerAffiliation.get(id);
    }

    public void setPlayerCharacter(Long id, String character) {
        playerCharacters.put(id, character);

    }

    public String getPlayerCharacter(Long id) {
        return playerCharacters.get(id);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

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
