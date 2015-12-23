/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.modes.client.core;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.projectsc.core.component.impl.ComponentListItem;
import de.projectsc.core.data.utils.Timer;
import de.projectsc.core.manager.ComponentManager;
import de.projectsc.core.manager.EntityManager;
import de.projectsc.core.manager.EventManager;
import de.projectsc.core.systems.physics.PhysicsSystem;
import de.projectsc.modes.client.core.data.ClientPlayer;
import de.projectsc.modes.client.core.states.MenuState;
import de.projectsc.modes.client.gui.GUICore;
import de.projectsc.modes.client.interfaces.ClientState;
import de.projectsc.modes.client.interfaces.GUI;
import de.projectsc.modes.client.messages.ClientMessage;

/**
 * Core class for the client.
 * 
 * @author Josch Bosch
 */
public class ClientCore implements Runnable {

    private static final int TICK_TIME = 16;

    private static final Log LOGGER = LogFactory.getLog(ClientCore.class);

    private BlockingQueue<ClientMessage> networkSendQueue;

    private BlockingQueue<ClientMessage> networkReceiveQueue;

    private ClientState currentState;

    @SuppressWarnings("unused")
    private final ClientPlayer player;

    private boolean clientRunning;

    private LinkedBlockingQueue<ClientMessage> userInputQueue;

    private GUI gui;

    private PhysicsSystem physicsSystem;

    private ComponentManager componentManager;

    private EntityManager entityManager;

    private EventManager eventManager;

    public ClientCore() {
        networkSendQueue = new LinkedBlockingQueue<>();
        networkReceiveQueue = new LinkedBlockingQueue<>();
        userInputQueue = new LinkedBlockingQueue<>();
        player = new ClientPlayer();
        componentManager = new ComponentManager();
        eventManager = new EventManager();
        entityManager = new EntityManager(componentManager, eventManager);
    }

    @Override
    public void run() {
        LOGGER.debug("Starting core ... ");
        Timer.init();
        loadComponents();
        loadSystems();
        gui = new GUICore(componentManager, entityManager, eventManager);
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
                while (Timer.getLag() >= TICK_TIME) {
                    physicsSystem.update(TICK_TIME);
                    currentState.loop(TICK_TIME);
                    Timer.setLag(Timer.getLag() - TICK_TIME);
                }
                currentState.loop(Timer.getLag());
                gui.render(currentState, null);
            }
            long timeNeeded = System.currentTimeMillis() - Timer.getSnapshotTime();
            long sleepTime = Math.max((16 - timeNeeded), 0L);
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

    private void loadSystems() {
        physicsSystem = new PhysicsSystem(entityManager, eventManager);
    }

    private void loadComponents() {
        for (ComponentListItem c : ComponentListItem.values()) {
            componentManager.registerComponent(c.getName(), c.getClazz());
        }
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
