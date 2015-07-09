/*
 * Copyright (C) 2015
 */

package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.server.core.AuthenticatedClient;
import de.projectsc.server.core.ServerCore;
import de.projectsc.server.core.serverMessages.NewClientConnectedServerMessage;
import de.projectsc.server.core.serverMessages.ServerMessage;
import de.projectsc.server.core.serverMessages.ServerMessageConstants;

public class ClientMock {

    private static final Log LOGGER = LogFactory.getLog(ClientMock.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private Map<Long, AuthendicatedClientMock> clients = new TreeMap<>();

    private void createNewClient(String[] information, BlockingQueue<ServerMessage> serverQueue) {
        AuthendicatedClientMock newClient = new AuthendicatedClientMock(information[1]);
        clients.put(newClient.getId(), newClient);
        newClient.start();
        LOGGER.debug("Created new client mock" + newClient.getId());
        serverQueue.add(new NewClientConnectedServerMessage(ServerMessageConstants.NEW_CLIENT_CONNECTED,
            newClient.getAuthenticatedClient(), null));

    }

    protected void createConsole(BlockingQueue<ServerMessage> serverQueue) {
        while (!shutdown.get()) {
            try {
                Thread.sleep(50);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();
                    LOGGER.debug("Got mock command: " + s);
                    String[] split = s.split("\\s");
                    if (split[0].equals(MessageConstants.SHUTDOWN)) {
                        shutdown.set(true);
                        serverQueue.put(new ServerMessage(MessageConstants.SHUTDOWN, null));
                        for (AuthendicatedClientMock client : clients.values()) {
                            client.kill();
                        }
                    } else if (split[0].equals("create-client")) {
                        if (split.length > 1) {
                            createNewClient(split, serverQueue);
                        } else {
                            LOGGER.debug("Could not create mock client: arguments invalid");
                        }

                    } else if (Character.isDigit(split[0].charAt(0))) {
                        Long id = null;
                        id = Long.parseLong(split[0]);
                        if (id != null) {
                            AuthendicatedClientMock client = clients.get(id);
                            if (client != null) {
                                if (split.length > 2) {
                                    client.sendMessage(new ServerMessage(split[1], s.substring(s.indexOf(split[2]))));
                                } else {
                                    client.sendMessage(new ServerMessage(split[1], null));
                                }
                            }
                        }
                    } else {
                        AuthendicatedClientMock client = null;
                        for (AuthendicatedClientMock c : clients.values()) {
                            if (c.getAuthenticatedClient().getDisplayName().equals(split[0])) {
                                client = c;
                            }
                        }
                        if (client != null) {
                            if (split.length > 2) {
                                client.sendMessage(new ServerMessage(split[1], s.substring(s.indexOf(split[2]))));
                            } else {
                                client.sendMessage(new ServerMessage(split[1], null));
                            }
                        } else {
                            serverQueue.put(new ServerMessage(split[0], null));
                        }
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
        new Thread(serverCore).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.createConsole(serverCore.getReceiveQueue());
            }

        }).start();

    }

}

class AuthendicatedClientMock {

    private static long idCount = 0;

    private static Log LOGGER = LogFactory.getLog(AuthendicatedClientMock.class);

    private long id;

    private boolean alive = true;

    private AuthenticatedClient authClient;

    private LinkedBlockingQueue<ServerMessage> sendToClientQueue;

    private LinkedBlockingQueue<ServerMessage> receiveFromClientQueue;

    public AuthendicatedClientMock(String name) {
        this.id = idCount++;

        sendToClientQueue = new LinkedBlockingQueue<ServerMessage>();
        receiveFromClientQueue = new LinkedBlockingQueue<ServerMessage>();
        authClient = new AuthenticatedClient(this.id, name, sendToClientQueue, receiveFromClientQueue);
    }

    public AuthenticatedClient getAuthenticatedClient() {
        return authClient;
    }

    public void start() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (alive) {
                    try {
                        ServerMessage msg = sendToClientQueue.take();
                        LOGGER.debug("Client " + authClient.getDisplayName() + ": Retreived message " + msg.toString());
                    } catch (InterruptedException e) {
                    }
                }

            }
        }).start();
    }

    public void sendMessage(ServerMessage message) {
        receiveFromClientQueue.offer(message);
    }

    public void kill() {
        alive = false;
    }

    public Long getId() {
        return id;
    }
}
