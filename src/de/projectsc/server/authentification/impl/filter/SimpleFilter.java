/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.impl.filter;

import de.projectsc.server.authentification.api.AuthentificationReader.Line;
import de.projectsc.server.authentification.api.Filter;

/**
 * 
 * Implementation of {@link Filter} to filter lines by column name.
 *
 * @author David Scholz
 */
public class SimpleFilter implements Filter {
    
    private String nameInLine;
    
    private String name;
    
    private boolean isValid;
    
    public SimpleFilter(String nameInLine, String name, boolean isValid) {
        this.nameInLine = nameInLine;
        this.name = name;
        this.isValid = isValid;
    }

    @Override
    public boolean isValid(Line l) {
        if (l.get(nameInLine).equals(name)) {
            return isValid;
        } else {
            return !isValid;
        }
        
    }

}
