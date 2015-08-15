/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.api;

import de.projectsc.server.authentification.api.AuthentificationReader.Line;

/**
 * 
 * Filters specific {@link Line}.
 *
 * @author David Scholz
 */
public interface Filter {
    
    boolean isValid(Line l);

}
