/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc;

import de.projectsc.client.core.ClientCore;
import de.projectsc.client.gui.GUICore;
import de.projectsc.client.network.ClientNetworkCore;

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

        if (args.length < 1 || args[0] == null || args[0].isEmpty()) {
            ClientCore core = new ClientCore();
            final GUICore gui = new GUICore(core.getGuiIncomingQueue(), core.getGuiOutgoingQueue());
            final ClientNetworkCore network = new ClientNetworkCore(core.getNetworkIncomingQueue(), core.getNetworkOutgoingQueue());
            new Thread(core).start();
            new Thread(network).start();
            new Thread(gui).start();
        } else {

        }
    }
}
