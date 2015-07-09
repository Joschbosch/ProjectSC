/*
 * Copyright (C) 2015 
 */

package de.projectsc.server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.server.core.serverMessages.ServerMessage;

public class ServerConsole implements Runnable {

    private static final Log LOGGER = LogFactory.getLog(ServerConsole.class);

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    private BlockingQueue<ServerMessage> coreQueue;

    public ServerConsole(BlockingQueue<ServerMessage> coreQueue) {
        // new Thread(this).start();
        this.coreQueue = coreQueue;
        LOGGER.debug("Console started.");
    }

    @Override
    public void run() {
        while (!shutdown.get()) {
            try {
                Thread.sleep(ServerConstants.SLEEPTIME);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();
                    LOGGER.debug("Got console command: " + s);
                    if (s.equals(MessageConstants.SHUTDOWN)) {
                        shutdown.set(true);
                    } else if (s.equals(ServerCommands.LISTCLIENTS)) {
                        shutdown.set(true);
                    }
                    coreQueue.offer(new ServerMessage(s, null));
                } catch (IOException e) {
                }
            } catch (InterruptedException e) {
            }
        }
    }

}
