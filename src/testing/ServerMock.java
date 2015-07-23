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
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.client.core.ClientCore;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.guiFake.FakeGUI;
import de.projectsc.client.network.ClientNetworkCore;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.core.game.GameConfiguration;
import de.projectsc.server.core.messages.ServerMessage;

public class ServerMock {

    private static final String INPUT_PREFIX = "input:";

    public static ServerMock mock;

    private static final Log LOGGER = LogFactory.getLog(ServerMock.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final Map<Long, AuthendicatedClientMock> clients = new TreeMap<>();

    public ShowPNG png;

    protected void createConsole(BlockingQueue<ClientMessage> send, Queue<ClientMessage> input) {
        while (!shutdown.get()) {
            try {
                Thread.sleep(50);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();
                    LOGGER.debug("Got mock command: " + s);
                    handleCommand(send, input, s);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
            }

        }
        LOGGER.debug(String.format("Mock console stopped"));

    }

    private void handleCommand(BlockingQueue<ClientMessage> clientQueue, Queue<ClientMessage> input, String s)
        throws InterruptedException {
        if (s != null && !s.isEmpty()) {
            String[] split = s.split("\\s");
            if (split[0].startsWith(INPUT_PREFIX)) {
                if (split.length > 1) {
                    input.add(new ClientMessage(split[0].replace(INPUT_PREFIX, ""), split[1]));
                } else {
                    input.add(new ClientMessage(split[0].replace(INPUT_PREFIX, "")));
                }
            } else if (split[0].equals(MessageConstants.SHUTDOWN)) {
                shutdown.set(true);
                clientQueue.put(new ClientMessage(MessageConstants.SHUTDOWN));
                for (AuthendicatedClientMock client : clients.values()) {
                    client.kill();
                }
            } else if (split[0].equals("create-client")) {
                if (split.length > 1) {
                    // createNewClient(split, clientQueue);
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
                if (split.length == 1) {
                    clientQueue.offer(new ClientMessage(split[0]));
                }
                if (split.length == 2) {
                    clientQueue.offer(new ClientMessage(split[0], split[1]));
                }
                if (split.length == 3) {
                    clientQueue.offer(new ClientMessage(split[0], split[1], split[2]));
                }
            }
        }

    }

    public static void main(String[] args) {
        mock = new ServerMock();
        ClientCore clientCore = new ClientCore();
        BlockingQueue<ClientMessage> fakeInternetQueue = new LinkedBlockingQueue<ClientMessage>();
        ClientNetworkCore network =
            new ClientNetworkCore(clientCore.getNetworkSendQueue(), clientCore.getNetworkReceiveQueue(), fakeInternetQueue);
        new Thread(clientCore).start();
        new Thread(network).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.createConsole(fakeInternetQueue, FakeGUI.input);
            }

        }).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        ClientMessage msg = clientCore.getNetworkSendQueue().take();
                        LOGGER.debug("Got message for server: " + msg);
                        if (msg.getMessage().equals(MessageConstants.CONNECT)) {
                            fakeInternetQueue.offer(new ClientMessage(MessageConstants.SERVER_WELCOME));
                        }
                        if (msg.getMessage().equals(MessageConstants.CLIENT_LOGIN_REQUEST)) {
                            if (msg.getData().length < 2) {
                                fakeInternetQueue.offer(new ClientMessage(MessageConstants.LOGIN_FAILED, "No credentials"));
                            } else if (msg.getData()[0].equals("josch") && msg.getData()[1].equals("josch")) {
                                fakeInternetQueue.offer(new ClientMessage(MessageConstants.LOGIN_SUCCESSFUL));
                            } else {
                                fakeInternetQueue.offer(new ClientMessage(MessageConstants.LOGIN_FAILED, "Invalid credentials"));
                            }
                        }
                        if (msg.getMessage().equals(MessageConstants.CREATE_NEW_GAME_REQUEST)) {
                            GameConfiguration gameConfig = new GameConfiguration();
                            gameConfig.setMapName("newDataMap");
                            gameConfig.setPlayerAffiliation(0L, (byte) 0);
                            gameConfig.setPlayerCharacter(0L, "person");

                            fakeInternetQueue.offer(new ClientMessage(MessageConstants.NEW_GAME_CREATED, gameConfig));

                        }
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getStackTrace());
                    }
                }
            }

        }).start();
        mock.png = new ShowPNG(null);
        mock.png.setVisible(true);

        try {
            mock.handleCommand(fakeInternetQueue, FakeGUI.input, "input:request:client_connect");
        } catch (InterruptedException e) {
        }

    }

}

class ShowClientPNG extends JFrame {

    public BufferedImage img = null;

    public BufferedImage img1 = null;

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public void setImg1(BufferedImage img1) {
        this.img1 = img1;
    }

    ShowClientPNG(final String arg2) {
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
