/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Configurations for a game.
 *
 * @author Josch Bosch
 */
public class GameConfiguration {

    private String mapName = "L1/first.map";

    private Map<String, String> playerCharacters;

    private Map<String, Byte> playerAffiliation;

    public GameConfiguration() {
        playerCharacters = new HashMap<>();
        playerAffiliation = new HashMap<>();
    }

    public GameConfiguration(Map<String, String> playerCharacters, Map<String, Byte> playerAffiliation) {
        this.playerCharacters = playerCharacters;
        this.playerAffiliation = playerAffiliation;
    }

    /**
     * 
     * @param id of the player
     * @param affiliation to set
     */
    public void setPlayerAffiliation(String id, byte affiliation) {
        playerAffiliation.put(id, affiliation);

    }

    /**
     * @param id of the player
     * @return current affiliation
     */
    public byte getPlayerAffiliation(String id) {
        return playerAffiliation.get(id);
    }

    /**
     * @param id of player
     * @param character chosen
     */
    public void setPlayerCharacter(String id, String character) {
        playerCharacters.put(id, character);

    }

    /**
     * 
     * @param id of player
     * @return current selected character
     */
    public String getPlayerCharacter(String id) {
        return playerCharacters.get(id);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setPlayerAffiliation(Map<String, Byte> playerAffiliation) {
        this.playerAffiliation = playerAffiliation;
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

    public void setPlayerCharacters(Map<String, String> playerCharacters) {
        this.playerCharacters = playerCharacters;
    }
}
