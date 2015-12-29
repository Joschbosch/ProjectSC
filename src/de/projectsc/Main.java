/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc;

import de.projectsc.modes.client.core.ClientCore;
import de.projectsc.modes.client.game.ClientGameCore;
import de.projectsc.modes.client.gui.GUICore;
import de.projectsc.modes.client.network.ClientNetworkCore;
import de.projectsc.modes.server.core.ServerCore;
import de.projectsc.modes.server.network.ServerNetworkCore;

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
        if (args.length > 0 && args[0] != null && args[0].equals("server")) {
            ServerCore serverCore = new ServerCore();
            new ServerNetworkCore(serverCore, serverCore.getReceiveQueue());
            new Thread(serverCore).start();

        } else {
            ClientCore clientCore = new ClientGameCore();
            GUICore gui =
                new GUICore(clientCore.getComponentManager(), clientCore.getEntityManager(), clientCore.getEventManager(),
                    clientCore.getTimer());
            clientCore.setGUI(gui);
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
