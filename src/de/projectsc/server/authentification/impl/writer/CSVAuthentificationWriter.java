/*
 * Project SC-2015 
 */
 
package de.projectsc.server.authentification.impl.writer;

import java.io.FileWriter;
import java.io.IOException;

import de.projectsc.server.authentification.api.AuthentificationWriter;
import de.projectsc.server.core.messages.AuthentificationRequestServerMessage;
import de.projectsc.server.core.messages.RequestEnum;

/**
 * 
 * CSV implementation of {@link AuthentificationWriter}.
 *
 * @author David Scholz
 */
public class CSVAuthentificationWriter implements AuthentificationWriter {
    
    private AuthentificationRequestServerMessage request;
    
    private FileWriter writer;
     
    public CSVAuthentificationWriter(AuthentificationRequestServerMessage request) {
        this.request = request;
    }

    @Override
    public void write() throws IOException {
        writer.write(request.getRequest().get(RequestEnum.NAME) + request.getRequest().get(RequestEnum.EMAIL) + request.getRequest().get(RequestEnum.PASSWORD));
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

}
