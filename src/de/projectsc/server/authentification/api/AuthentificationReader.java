/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.api;

/**
 * 
 * Reader interface.
 *
 * @author David Scholz
 */
public interface AuthentificationReader {

    /**
     * 
     * Represents a line.
     *
     * @author David Scholz
     */
    public interface Line {
        String get(String columnName);
    }
    
    Line next();
    
    void close();
    
}
