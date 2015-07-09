/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

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
        ServerCore serverCore = new ServerCore();
        ServerNetworkCore serverNetwork = new ServerNetworkCore(serverCore, serverCore.getReceiveQueue());
        new Thread(serverCore).start();
        new Thread(serverNetwork).start();

        // final GUICore gui = new GUICore(core.getGuiIncomingQueue(), core.getGuiOutgoingQueue());
        // final ClientNetworkCore network =
        // new ClientNetworkCore(core.getNetworkSendQueue(), core.getNetworkReceiveQueue(), serverNetwork.clientSendQueueFaking,
        // serverNetwork.clientReceiveQueueFaking);
        // new Thread(core).start();
        // new Thread(network).start();
        // new Thread(gui).start();

        // while (true) {
        // try {
        // Thread.sleep(100);
        // LogFactory.getLog(Main.class).debug(serverCore.getNetworkSendQueue().size() + "  " +
        // serverCore.getNetworkReceiveQueue().size() + "  "
        // + core.getNetworkSendQueue().size() + "  " + core.getNetworkReceiveQueue().size() + "  "
        // + core.getGuiOutgoingQueue().size() + "  " + core.getGuiIncomingQueue().size());
        // LogFactory.getLog(Main.class).debug(serverNetwork.clientSendQueueFaking.size() + "  " +
        // serverNetwork.clientReceiveQueueFaking.size());
        // } catch (InterruptedException e) {
        // }
        // }
    }
}
