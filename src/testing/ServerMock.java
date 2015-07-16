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

import de.projectsc.client.core.ClientCore;
import de.projectsc.client.core.messages.ClientMessage;
import de.projectsc.client.network.ClientNetworkCore;
import de.projectsc.core.data.messages.MessageConstants;
import de.projectsc.server.core.messages.ServerMessage;

public class ServerMock {

    public static ServerMock mock;

    private static final Log LOGGER = LogFactory.getLog(ServerMock.class);

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final Map<Long, AuthendicatedClientMock> clients = new TreeMap<>();

    public ShowPNG png;

    protected void createConsole(BlockingQueue<ClientMessage> send, BlockingQueue<ClientMessage> guiSendMessage) {
        while (!shutdown.get()) {
            try {
                Thread.sleep(50);
                try {
                    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                    String s = bufferRead.readLine();
                    LOGGER.debug("Got mock command: " + s);
                    handleCommand(send, guiSendMessage, s);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
            }

        }
        LOGGER.debug(String.format("Mock console stopped"));

    }

    private void handleCommand(BlockingQueue<ClientMessage> clientQueue, BlockingQueue<ClientMessage> guiSendMessage, String s)
        throws InterruptedException {
        String[] split = s.split("\\s");
        if (split[0].startsWith("input:")) {
            String[] split2 = split[0].split(":");
            if (split.length > 1) {
                guiSendMessage.put(new ClientMessage(split[0], split[1]));
            } else {
                guiSendMessage.put(new ClientMessage(split[0]));
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
                clientQueue.put(new ClientMessage(split[0]));
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
        BlockingQueue<ClientMessage> guiSendMessage = clientCore.getUserInputQueue();
        new Thread(new Runnable() {

            @Override
            public void run() {
                mock.createConsole(fakeInternetQueue, guiSendMessage);
            }

        }).start();
        mock.png = new ShowPNG(null);
        mock.png.setVisible(true);

        // mock.handleCommand(queue, "create-client Josch");
        // mock.handleCommand(queue, "create-client Ilka");
        // mock.handleCommand(queue, "create-client Client1");
        // mock.handleCommand(queue, "create-client Client2");
        // mock.handleCommand(queue, "create-client Client3");
        // mock.handleCommand(queue, "create-client Client4");
        // mock.handleCommand(queue, "create-client Client5");
        // mock.handleCommand(queue, "create-client Client6");
        // mock.handleCommand(queue, "Josch request:create_new_game");
        // mock.handleCommand(queue, "Ilka request:join_game 1000");
        // mock.handleCommand(queue, "Client1 request:join_game 1000");
        // mock.handleCommand(queue, "Client2 request:join_game 1000");
        // mock.handleCommand(queue, "Client3 request:join_game 1000");
        // mock.handleCommand(queue, "Client4 request:join_game 1000");
        // mock.handleCommand(queue, "Client5 request:join_game 1000");
        // mock.handleCommand(queue, "Client6 request:join_game 1000");
        // Thread.sleep(2000);
        // mock.handleCommand(queue, "Josch request:start_game");
        // Thread.sleep(2000);
        // mock.handleCommand(queue, "Josch update_loading_progress 100");
        // mock.handleCommand(queue, "Ilka update_loading_progress 100");
        // mock.handleCommand(queue, "Client1 update_loading_progress 100");
        // mock.handleCommand(queue, "Client2 update_loading_progress 100");
        // mock.handleCommand(queue, "Client3 update_loading_progress 100");
        // mock.handleCommand(queue, "Client4 update_loading_progress 100");
        // mock.handleCommand(queue, "Client5 update_loading_progress 100");
        // mock.handleCommand(queue, "Client6 update_loading_progress 100");
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
