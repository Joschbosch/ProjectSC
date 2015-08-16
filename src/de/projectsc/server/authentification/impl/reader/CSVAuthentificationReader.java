/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.impl.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.server.authentification.api.AuthentificationReader;
import de.projectsc.server.authentification.api.Filter;
import de.projectsc.server.authentification.database.DatabaseConnection;

/**
 * 
 * CSV implementation of {@link AuthentificationReader}.
 *
 * @author David Scholz
 */
public class CSVAuthentificationReader implements AuthentificationReader {
    
    private static final Log LOGGER = LogFactory.getLog(DatabaseConnection.class);
    
    private BufferedReader reader;
    
    private Map<String, Integer> header;
    
    private List<Filter> filterList;

    public CSVAuthentificationReader(Reader filereader, List<Filter> filterList) throws IOException {
        this.reader = new BufferedReader(filereader);
        this.filterList = filterList;
        
        header = new LinkedHashMap<>();
        
        String[] firstLine = reader.readLine().split(";");
        
        if (firstLine == null) {
            throw new IOException("File is empty.");
        }
        
        for (int dataPos = 0; dataPos < firstLine.length; dataPos++) {
            header.put(firstLine[dataPos], dataPos);
        }
        
    }
    
    @Override
    public Line next() {
        
        while(true) {
            try {
                final String line = reader.readLine();
                
                if (line == null) {
                    break;
                }
                
                Line l = new Line() {

                    @Override
                    public String get(String columnName) {
                        return line.split(";")[header.get(columnName)];
                    }                    
                };
                boolean isValid = true;
                for (Filter filter : filterList) {
                    if (!filter.isValid(l)) {
                        isValid = false;
                    } 
                }
                
                if (isValid) {
                    return l;
                }
                
            } catch (IOException e) {
                LOGGER.error("Failed to read line.");
            }
        }
        
        
        return null;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
    
    public Set<String> getHeader() {
        return header.keySet();
    }

}
