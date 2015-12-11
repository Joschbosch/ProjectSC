/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.messages.MessageConstants;
import de.projectsc.modes.server.core.messages.ServerMessage;

/**
 * Read commands for the server.
 * 
 * @author Josch Bosch
 */
public class ServerConsole implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ServerConsole.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final BlockingQueue<ServerMessage> coreQueue;

    public ServerConsole(BlockingQueue<ServerMessage> coreQueue) {
        new Thread(this).start();
        this.coreQueue = coreQueue;
        LOGGER.debug("Console started.");
    }

    @Override
    public void run() {
        LOGGER.debug(String.format("Server console started"));

        while (!shutdown.get()) {
            try {
                Thread.sleep(ServerConstants.SLEEPTIME);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();
                    LOGGER.debug("Got console command: " + s);
                    if (s.equals(MessageConstants.SHUTDOWN)) {
                        coreQueue.offer(new ServerMessage(MessageConstants.SHUTDOWN));
                        shutdown.set(true);
                    } else if (s.equals(ServerCommands.LISTCLIENTS)) {
                        shutdown.set(true);
                    }
                    coreQueue.offer(new ServerMessage(s));
                } catch (IOException e) {
                }
            } catch (InterruptedException e) {
            }
        }
        LOGGER.debug(String.format("Server console stopped"));

    }

}
