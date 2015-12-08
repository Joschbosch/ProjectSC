/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.modes.client.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.data.Timer;
import de.projectsc.core.modes.client.common.ClientState;
import de.projectsc.core.modes.client.common.GUI;
import de.projectsc.core.modes.client.common.data.ClientPlayer;
import de.projectsc.core.modes.client.common.messages.ClientMessage;
import de.projectsc.core.modes.client.core.states.MenuState;
import de.projectsc.core.modes.client.gui.GUICore;
import de.projectsc.core.modes.server.core.game.GameRunningState;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class ClientCore implements Runnable {

    private static final int TICK_TIME = (int) GameRunningState.GAME_TICK_TIME;

    private static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    private BlockingQueue<ClientMessage> networkSendQueue;

    private BlockingQueue<ClientMessage> networkReceiveQueue;

    private ClientState currentState;

    private final ClientPlayer player;

    private boolean clientRunning;

    private LinkedBlockingQueue<ClientMessage> userInputQueue;

    private GUI gui;

    public ClientCore() {
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
        userInputQueue = new LinkedBlockingQueue<>();
        player = new ClientPlayer();
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        Timer.init();
        gui = new GUICore();
        gui.initCore();
        clientRunning = true;
        currentState = new MenuState();
        currentState.init(gui);
        LOGGER.debug(String.format("Client started"));
        while (clientRunning) {
            Timer.update();
            readServerMessages();
            if (currentState != null) {
                Map<Integer, Integer> keyMap = gui.readInput();
                currentState.handleInput(keyMap);
                long lag = Timer.getLag();
                while (lag >= TICK_TIME) {
                    currentState.loop(TICK_TIME);
                    lag -= TICK_TIME;
                }
                currentState.loop(Timer.getLag());
                gui.render(currentState, null);
            }
            long timeNeeded = System.currentTimeMillis() - Timer.getSnapshotTime();
            long sleepTime = Math.max((GameRunningState.GAME_TICK_TIME - timeNeeded), 0L);
            // LOGGER.debug(
            // String.format("Game %d needed %d ms for current tick, will sleep : %d",
            // 1, timeNeeded, sleepTime));
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                LOGGER.debug(e);
            }
            if (!gui.isRunning()) {
                clientRunning = false;
                System.exit(0);
            }
        }
        LOGGER.debug(String.format("Client terminated"));

    }

    private void readServerMessages() {
        if (currentState != null) {
            while (!networkReceiveQueue.isEmpty()) {
                ClientMessage msg = networkReceiveQueue.poll();
                LOGGER.debug("Got message from server: " + msg);
                currentState.handleMessage(msg);
            }
        }
    }

    /**
     * @param clientMessage to send to server
     */
    public void sendMessageToServer(ClientMessage clientMessage) {
        networkSendQueue.offer(clientMessage);
    }

    public BlockingQueue<ClientMessage> getNetworkSendQueue() {
        return networkSendQueue;
    }

    public void setNetworkSendQueue(BlockingQueue<ClientMessage> networkIncomingQueue) {
        this.networkSendQueue = networkIncomingQueue;
    }

    public BlockingQueue<ClientMessage> getNetworkReceiveQueue() {
        return networkReceiveQueue;
    }

    public void setNetworkReceiveQueue(BlockingQueue<ClientMessage> networkOutgoingQueue) {
        this.networkReceiveQueue = networkOutgoingQueue;
    }

    public LinkedBlockingQueue<ClientMessage> getUserInputQueue() {
        return userInputQueue;
    }

    public void setUserInputQueue(LinkedBlockingQueue<ClientMessage> userInputQueue) {
        this.userInputQueue = userInputQueue;
    }

}
