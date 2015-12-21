/*
 * Copyright (C) 2015
 */

package de.projectsc.modes.server.core.data.statistics;

/**
 * Data of the current player.
 * 
 * @author Josch Bosch
 */
public class PlayerStatistics {

    private int gamesPlayed = 0;

    private int level = 0;

    private int experience = 0;

    private int kills = 0;

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

}
