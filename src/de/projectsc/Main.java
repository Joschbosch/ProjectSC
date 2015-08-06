/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import de.projectsc.client.core.ClientCore;
import de.projectsc.client.network.ClientNetworkCore;
import de.projectsc.server.core.ServerCore;
import de.projectsc.server.network.ServerNetworkCore;

/**
 * Main class.
 * 
 * @author Josch Bosch
 */
public class Main {

    protected Main() {

    }

    /**
     * Start of the game.
     * 
     * @param args command line parameters
     */
    public static void main(String[] args) {
        // ClientCore core = new ClientCore();
        if (args.length > 0 && args[0] != null && args[0].equals("server")) {
            ServerCore serverCore = new ServerCore();
            new ServerNetworkCore(serverCore, serverCore.getReceiveQueue());
            new Thread(serverCore).start();

        } else {
            ClientCore clientCore = new ClientCore();
            ClientNetworkCore network =
                new ClientNetworkCore(clientCore.getNetworkSendQueue(), clientCore.getNetworkReceiveQueue(), null);
            new Thread(clientCore).start();
            new Thread(network).start();

        }

        // final GUICore gui = new GUICore(core.getGuiIncomingQueue(), core.getGuiOutgoingQueue());
        // final ClientNetworkCore network =
        // new ClientNetworkCore(core.getNetworkSendQueue(), core.getNetworkReceiveQueue(),
        // serverNetwork.clientSendQueueFaking,
        // serverNetwork.clientReceiveQueueFaking);
        // new Thread(core).start();
        // new Thread(network).start();
        // new Thread(gui).start();

        // while (true) {
        // try {
        // Thread.sleep(100);
        // LogFactory.getLog(Main.class).debug(serverCore.getNetworkSendQueue().size() + " " +
        // serverCore.getNetworkReceiveQueue().size() + " "
        // + core.getNetworkSendQueue().size() + " " + core.getNetworkReceiveQueue().size() + " "
        // + core.getGuiOutgoingQueue().size() + " " + core.getGuiIncomingQueue().size());
        // LogFactory.getLog(Main.class).debug(serverNetwork.clientSendQueueFaking.size() + " " +
        // serverNetwork.clientReceiveQueueFaking.size());
        // } catch (InterruptedException e) {
        // }
        // }
    }
}
