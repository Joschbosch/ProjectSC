/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.api;

import java.io.IOException;

/**
 * 
 * Writer Interface.
 *
 * @author David Scholz
 */
public interface AuthentificationWriter {
    
    void write() throws IOException;
    
    void close() throws IOException;

}
