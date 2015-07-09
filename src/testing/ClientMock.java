/*
 * Copyright (C) 2015
 */

package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.data.messages.NetworkMessage;
import de.projectsc.server.core.ServerCore;
import de.projectsc.server.network.ServerNetworkCore;

public class ClientMock {

    private static final Log LOGGER = LogFactory.getLog(ClientMock.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private void receiveServerMessages(BlockingQueue<NetworkMessage> clientSendQueueFaking) {
        while (!shutdown.get()) {
            try {
                Thread.sleep(50);
                while (!clientSendQueueFaking.isEmpty()) {
                    NetworkMessage msg;
                    try {
                        msg = clientSendQueueFaking.take();
                        if (MessageConstants.CLOSE_DOWN.equals(msg.getMessage())) {
                        } else {
                        }
                        LOGGER.debug("Received: " + msg.getMessage());
                    } catch (InterruptedException e) {
                        LOGGER.error("Error reading core messages: ", e);
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }

    protected void createConsole(BlockingQueue<NetworkMessage> clientSendQueueFaking,
        BlockingQueue<NetworkMessage> clientReceiveQueueFaking) {
        while (!shutdown.get()) {
            try {
                Thread.sleep(50);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();

                    clientReceiveQueueFaking.offer(new NetworkMessage(s, null));
                    if (s.equals(MessageConstants.CLOSE_DOWN)) {
                        shutdown.set(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
            }
        }

    }

    public static void main(String[] args) {
        ClientMock mock = new ClientMock();
        ServerCore serverCore = new ServerCore();
        ServerNetworkCore serverNetwork =
            new ServerNetworkCore(serverCore.getNetworkSendQueue(), serverCore.getNetworkReceiveQueue());
        new Thread(serverCore).start();
        new Thread(serverNetwork).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.receiveServerMessages(serverNetwork.clientSendQueueFaking);
            }

        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.createConsole(serverNetwork.clientSendQueueFaking, serverNetwork.clientReceiveQueueFaking);
            }

        }).start();

    }

}
