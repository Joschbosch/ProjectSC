/*
 * Copyright (C) 2016 
 */

package de.projectsc;

/**
 * Class for specifing the global and area wide debug level.
 * 
 * @author Josch Bosch
 */
public final class Debug {

    /**
     * Debug value for no debug at all.
     */
    public static final int DEBUG_NOTHING = 0;

    /**
     * Do all debug output for message.
     */
    public static final int DEBUG_ALL = 100;

    /**
     * Global debug level.
     */
    public static int debugLevel = 0;

    /**
     * Debug level for all GUI things.
     */
    public static int guiDebugLevel = 0;

    /**
     * Network debug level.
     */
    public static int networkDebugLevel = 0;

    /**
     * Core debug level.
     */
    public static int coreDebugLevel = 0;

    private Debug() {

    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static int getGuiDebugLevel() {
        return guiDebugLevel;
    }

    public static int getNetworkDebugLevel() {
        return networkDebugLevel;
    }

    public static int getCoreDebugLevel() {
        return coreDebugLevel;
    }

    public static void setCoreDebugLevel(int coreDebugLevel) {
        Debug.coreDebugLevel = coreDebugLevel;
    }

    public static void setDebugLevel(int debugLevel) {
        Debug.debugLevel = debugLevel;
    }

    public static void setGuiDebugLevel(int guiDebugLevel) {
        Debug.guiDebugLevel = guiDebugLevel;
    }

    public static void setNetworkDebugLevel(int networkDebugLevel) {
        Debug.networkDebugLevel = networkDebugLevel;
    }

}
