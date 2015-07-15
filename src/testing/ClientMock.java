/*
 * Copyright (C) 2015
 */

package testing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.server.core.AuthenticatedClient;
import de.projectsc.server.core.ServerCore;
import de.projectsc.server.core.messages.NewClientConnectedServerMessage;
import de.projectsc.server.core.messages.ServerMessage;
import de.projectsc.server.core.messages.ServerMessageConstants;

public class ClientMock {

    public static ClientMock mock;

    private static final Log LOGGER = LogFactory.getLog(ClientMock.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final Map<Long, AuthendicatedClientMock> clients = new TreeMap<>();

    public ShowPNG png;

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
                    handleCommand(serverQueue, s);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
            }

        }
        LOGGER.debug(String.format("Mock console stopped"));

    }

    private void handleCommand(BlockingQueue<ServerMessage> serverQueue, String s) throws InterruptedException {
        String[] split = s.split("\\s");
        if (split[0].equals(MessageConstants.SHUTDOWN)) {
            shutdown.set(true);
            serverQueue.put(new ServerMessage(MessageConstants.SHUTDOWN));
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
                        client.sendMessage(new ServerMessage(split[1]));
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
                    client.sendMessage(new ServerMessage(split[1]));
                }
            } else {
                serverQueue.put(new ServerMessage(split[0]));
            }
        }
    }

    public static void main(String[] args) {
        mock = new ClientMock();
        ServerCore serverCore = new ServerCore();
        new Thread(serverCore).start();

        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.createConsole(serverCore.getReceiveQueue());
            }

        }).start();
        mock.png = new ShowPNG(null);
        mock.png.setVisible(true);
        try {
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Josch");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Ilka");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client1");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client2");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client3");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client4");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client5");
            mock.handleCommand(serverCore.getReceiveQueue(), "create-client Client6");
            mock.handleCommand(serverCore.getReceiveQueue(), "Josch request:create_new_game");
            mock.handleCommand(serverCore.getReceiveQueue(), "Ilka request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client1 request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client2 request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client3 request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client4 request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client5 request:join_game 1000");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client6 request:join_game 1000");
            Thread.sleep(2000);
            mock.handleCommand(serverCore.getReceiveQueue(), "Josch request:start_game");
            Thread.sleep(2000);
            mock.handleCommand(serverCore.getReceiveQueue(), "Josch update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Ilka update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client1 update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client2 update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client3 update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client4 update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client5 update_loading_progress 100");
            mock.handleCommand(serverCore.getReceiveQueue(), "Client6 update_loading_progress 100");
        } catch (InterruptedException e) {
        }
    }

}

class ShowPNG extends JFrame {

    public BufferedImage img = null;

    public BufferedImage img1 = null;

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public void setImg1(BufferedImage img1) {
        this.img1 = img1;
    }

    ShowPNG(final String arg2) {
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null && img1 != null) {
                    g.drawImage(img, 0, 0, null);
                    g.drawImage(img1, img.getWidth(), 0, null);
                }
            }
        };
        panel.setSize(2048, 1024);

        this.setSize(2048, 1024);
        this.getContentPane().add(panel);
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                    }
                    repaint();
                }
            }
        }).start();
    }
}

class AuthendicatedClientMock {

    private static long idCount = 0;

    private static Log LOGGER = LogFactory.getLog(AuthendicatedClientMock.class);

    private final long id;

    private boolean alive = true;

    private final AuthenticatedClient authClient;

    private final LinkedBlockingQueue<ServerMessage> sendToClientQueue;

    private final LinkedBlockingQueue<ServerMessage> receiveFromClientQueue;

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
                        if (msg.getMessage().equals("newImage")) {
                            ClientMock.mock.png.setImg((BufferedImage) msg.getData()[0]);
                            ClientMock.mock.png.setImg1((BufferedImage) msg.getData()[1]);
                        }
                    } catch (InterruptedException e) {
                    }
                }
                LOGGER.debug("Client closed: " + authClient.getDisplayName());
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
